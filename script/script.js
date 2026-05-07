// ── DATA ───────────────────────────────────────────────────────────

const HOUSES = [
  { n:"Old Shack",           e:"🏚️", cost:0    },
  { n:"Small House",         e:"🏠",  cost:100  },
  { n:"Cozy House",          e:"🏡",  cost:200  },
  { n:"Nice House",          e:"🏘️", cost:300  },
  { n:"Under Construction",  e:"🏗️", cost:400  },
  { n:"Building",            e:"🏢",  cost:500  },
  { n:"Department Store",    e:"🏬",  cost:600  },
  { n:"Grand House",         e:"🏛️", cost:700  },
  { n:"Castle",              e:"🏰",  cost:800  },
  { n:"Palace",              e:"🏯",  cost:900  },
  { n:"Stadium",             e:"🏟️", cost:1000 },
  { n:"Beach House",         e:"🏖️", cost:1200 },
  { n:"Island House",        e:"🏝️", cost:1400 },
  { n:"Mountain House",      e:"🏞️", cost:1600 },
  { n:"City Penthouse",      e:"🌃",  cost:1800 },
  { n:"Skyline Villa",       e:"🌆",  cost:2000 },
  { n:"Sunset Mansion",      e:"🌇",  cost:2500 },
  { n:"Metro Tower",         e:"🏙️", cost:3000 },
  { n:"Luxury Hotel",        e:"🏩",  cost:4000 },
  { n:"Mansion with Pool",   e:"🏊",  cost:5000 }
];

const SHOP_ITEMS = [
  { id:"hat",      cat:"clothes", n:"Straw Hat",    e:"👒", cost:50  },
  { id:"cap",      cat:"clothes", n:"Cool Cap",     e:"🧢", cost:75  },
  { id:"shirt",    cat:"clothes", n:"Red Shirt",    e:"👕", cost:100 },
  { id:"overalls", cat:"clothes", n:"Overalls",     e:"👖", cost:120 },
  { id:"woven",    cat:"basket",  n:"Woven Basket", e:"🧺", cost:80  },
  { id:"bucket",   cat:"basket",  n:"Metal Bucket", e:"🪣", cost:100 },
  { id:"golden",   cat:"basket",  n:"Gold Basket",  e:"🎁", cost:200 }
];

const FRUITS = [
  { e:"🍎", pts:10,  w:40, type:"normal" },
  { e:"🍌", pts:10,  w:40, type:"normal" },
  { e:"🍇", pts:10,  w:30, type:"normal" },
  { e:"🍊", pts:10,  w:35, type:"normal" },
  { e:"✨", pts:50,  w:5,  type:"star"   },
  { e:"💣", pts:-30, w:12, type:"bomb"   },
  { e:"🤢", pts:-20, w:15, type:"rotten" }
];

// ── STATE ──────────────────────────────────────────────────────────

let state = {
  char:"male", level:1, score:0, coins:0,
  timer:180, houseStage:0, owned:{}, running:false
};
let fruits = [], gameLoop = null, timerInt = null, spawnTO = null;
let arenaW = 0, arenaH = 0, mouseX = 200, smoothX = 200, lastT = 0;

// ── STARS BACKGROUND ───────────────────────────────────────────────

(function initStars() {
  const canvas = document.getElementById('stars-canvas');
  const ctx = canvas.getContext('2d');
  let stars = [];

  function resize() {
    canvas.width  = window.innerWidth;
    canvas.height = window.innerHeight;
    stars = Array.from({ length: 120 }, () => ({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      r: Math.random() * 1.4 + 0.3,
      a: Math.random(),
      speed: Math.random() * 0.008 + 0.003,
      phase: Math.random() * Math.PI * 2
    }));
  }

  function drawStars(t) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    stars.forEach(s => {
      const alpha = 0.3 + 0.7 * Math.abs(Math.sin(t * s.speed + s.phase));
      ctx.beginPath();
      ctx.arc(s.x, s.y, s.r, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(255,255,255,${alpha.toFixed(2)})`;
      ctx.fill();
    });
    requestAnimationFrame(drawStars);
  }

  resize();
  window.addEventListener('resize', resize);
  requestAnimationFrame(drawStars);
})();

// ── PARTICLE SYSTEM ────────────────────────────────────────────────

let pCanvas, pCtx, particles = [];

function initParticles() {
  pCanvas = document.getElementById('particle-canvas');
  pCtx    = pCanvas.getContext('2d');
  resizeParticleCanvas();
}

function resizeParticleCanvas() {
  if (!pCanvas) return;
  pCanvas.width  = arenaW;
  pCanvas.height = arenaH;
}

function spawnParticles(x, y, color, count, type) {
  for (let i = 0; i < count; i++) {
    const angle  = (Math.PI * 2 / count) * i + Math.random() * 0.5;
    const speed  = type === 'bomb' ? (4 + Math.random() * 5) : (2 + Math.random() * 3);
    const size   = type === 'bomb' ? (4 + Math.random() * 6) : (3 + Math.random() * 4);
    particles.push({
      x, y,
      vx: Math.cos(angle) * speed,
      vy: Math.sin(angle) * speed - (type === 'bomb' ? 0 : 2),
      color,
      size,
      life: 1,
      decay: type === 'bomb' ? 0.025 : 0.035,
      gravity: type === 'bomb' ? 0.15 : 0.08,
      shape: type === 'bomb' ? 'square' : 'circle'
    });
  }
}

function spawnShockwave(x, y) {
  particles.push({ type:'shockwave', x, y, r:10, maxR:90, life:1, decay:0.055 });
}

function updateParticles() {
  if (!pCtx || !pCanvas) return;
  pCtx.clearRect(0, 0, pCanvas.width, pCanvas.height);
  for (let i = particles.length - 1; i >= 0; i--) {
    const p = particles[i];
    p.life -= p.decay;
    if (p.life <= 0) { particles.splice(i, 1); continue; }

    if (p.type === 'shockwave') {
      p.r += (p.maxR - p.r) * 0.18;
      pCtx.beginPath();
      pCtx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
      pCtx.strokeStyle = `rgba(255,100,30,${p.life.toFixed(2)})`;
      pCtx.lineWidth = 3;
      pCtx.stroke();
      continue;
    }

    p.vy += p.gravity;
    p.x  += p.vx;
    p.y  += p.vy;
    p.vx *= 0.97;

    pCtx.globalAlpha = p.life;
    pCtx.fillStyle   = p.color;
    if (p.shape === 'square') {
      const s = p.size * p.life;
      pCtx.fillRect(p.x - s/2, p.y - s/2, s, s);
    } else {
      pCtx.beginPath();
      pCtx.arc(p.x, p.y, p.size * p.life, 0, Math.PI * 2);
      pCtx.fill();
    }
    pCtx.globalAlpha = 1;
  }
}

// ── SCREEN NAVIGATION ──────────────────────────────────────────────

function showScreen(id) {
  document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
  document.getElementById(id).classList.add('active');
}

// ── CHARACTER SELECT ───────────────────────────────────────────────

function selectChar(c) {
  state.char = c;
  document.getElementById('cc-male').classList.toggle('selected', c === 'male');
  document.getElementById('cc-female').classList.toggle('selected', c === 'female');
  document.getElementById('startBtn').disabled = false;
}

// ── GAME START / RETRY ─────────────────────────────────────────────

function startGame() {
  state.level = 1; state.score = 0; state.timer = 180;
  state.owned = {}; state.coins = 0; state.houseStage = 0;
  fruits = [];
  showScreen('gamescreen');
  setTimeout(initGame, 80);
}

function retryGame() {
  state.level = 1; state.score = 0; state.timer = 180;
  fruits = [];
  showScreen('gamescreen');
  setTimeout(initGame, 80);
}

// ── GAME INIT ──────────────────────────────────────────────────────

function initGame() {
  const arena = document.getElementById('arena');
  arenaW = arena.offsetWidth;
  arenaH = arena.offsetHeight;
  mouseX = arenaW / 2;
  smoothX = mouseX;

  document.getElementById('farmer').textContent = state.char === 'male' ? '👨‍🌾' : '👩‍🌾';

  clearAll();
  fruits.forEach(f => f.el && f.el.parentNode && f.el.parentNode.removeChild(f.el));
  fruits = [];
  particles = [];

  initParticles();
  state.running = true;
  updateHud();

  timerInt = setInterval(tickTimer, 1000);
  scheduleSpawn();
  lastT = 0;
  gameLoop = requestAnimationFrame(frame);

  arena.onmousemove = e => {
    const r = arena.getBoundingClientRect();
    mouseX = e.clientX - r.left;
  };
  arena.ontouchmove = e => {
    e.preventDefault();
    const r = arena.getBoundingClientRect();
    mouseX = e.touches[0].clientX - r.left;
  };
  arena.ontouchstart = e => {
    const r = arena.getBoundingClientRect();
    mouseX = e.touches[0].clientX - r.left;
  };
}

// ── CLEAR INTERVALS ────────────────────────────────────────────────

function clearAll() {
  if (timerInt) { clearInterval(timerInt);       timerInt = null; }
  if (spawnTO)  { clearTimeout(spawnTO);          spawnTO  = null; }
  if (gameLoop) { cancelAnimationFrame(gameLoop); gameLoop = null; }
}

// ── FRUIT SPAWNING ─────────────────────────────────────────────────

function scheduleSpawn() {
  const base  = Math.max(400, 1800 - state.level * 80);
  const delay = base + Math.random() * 600;
  spawnTO = setTimeout(() => {
    if (state.running) { spawnFruit(); scheduleSpawn(); }
  }, delay);
}

function spawnFruit() {
  const total = FRUITS.reduce((a, f) => a + f.w, 0);
  let r = Math.random() * total;
  let chosen = FRUITS[0];
  for (const f of FRUITS) { r -= f.w; if (r <= 0) { chosen = f; break; } }

  const el = document.createElement('div');
  el.className = 'fruit';
  el.textContent = chosen.e;

  const x = 20 + Math.random() * (arenaW - 60);
  el.style.left = x + 'px';
  el.style.top  = '-44px';

  // Wobble rotation on fruit
  el.style.animation = `fruitWobble ${0.6 + Math.random() * 0.6}s ease-in-out infinite alternate`;

  document.getElementById('arena').appendChild(el);
  const speed = 1.8 + state.level * 0.35 + Math.random() * 1.2;
  fruits.push({ el, x, y: -44, pts: chosen.pts, speed, type: chosen.type });
}

// Inject wobble keyframe once
(function() {
  const s = document.createElement('style');
  s.textContent = '@keyframes fruitWobble{from{transform:rotate(-8deg)}to{transform:rotate(8deg)}}';
  document.head.appendChild(s);
})();

// ── GAME LOOP ──────────────────────────────────────────────────────

function frame(t) {
  if (!state.running) return;
  const dt = Math.min(t - lastT, 50);
  lastT = t;

  smoothX += (mouseX - smoothX) * 0.16;
  const bx = Math.max(30, Math.min(arenaW - 30, smoothX));

  const farmer = document.getElementById('farmer');
  const basket = document.getElementById('basket');

  farmer.style.left   = bx + 'px';
  basket.style.left   = bx + 'px';
  basket.style.bottom = (60 + 48) + 'px';

  const catchY = arenaH - (60 + 48 + 38);

  for (let i = fruits.length - 1; i >= 0; i--) {
    const f = fruits[i];
    f.y += f.speed * (dt / 16);
    f.el.style.top = f.y + 'px';

    const fx = f.x + 15;
    const fy = f.y + 15;

    if (Math.abs(fx - bx) < 46 && fy >= catchY - 10 && fy <= catchY + 44) {
      collectFruit(f, bx, catchY);
      fruits.splice(i, 1);
    } else if (f.y > arenaH) {
      // Missed bad fruit = no penalty, missed good fruit = subtle puff
      if (f.type === 'bomb' || f.type === 'rotten') {
        spawnParticles(f.x + 15, arenaH - 64, '#555', 6, 'normal');
      }
      if (f.el.parentNode) f.el.parentNode.removeChild(f.el);
      fruits.splice(i, 1);
    }
  }

  updateParticles();
  gameLoop = requestAnimationFrame(frame);
}

// ── COLLECT FRUIT ──────────────────────────────────────────────────

function collectFruit(f, bx, by) {
  if (f.el.parentNode) f.el.parentNode.removeChild(f.el);

  state.score = Math.max(0, state.score + f.pts);
  if (f.pts > 0) state.coins += Math.ceil(f.pts / 5);

  if (f.type === 'bomb') {
    // Big explosion
    spawnShockwave(bx, by);
    spawnParticles(bx, by, '#ff6f00', 18, 'bomb');
    spawnParticles(bx, by, '#ffd54f', 12, 'bomb');
    spawnParticles(bx, by, '#ef5350', 10, 'bomb');
    screenShake();
    showFx(bx, by, '💥 -30', '#ef5350');
  } else if (f.type === 'rotten') {
    spawnParticles(bx, by, '#8bc34a', 10, 'normal');
    spawnParticles(bx, by, '#33691e', 8,  'normal');
    showFx(bx, by, '🤢 -20', '#aed581');
  } else if (f.type === 'star') {
    spawnParticles(bx, by, '#ffd54f', 16, 'normal');
    spawnParticles(bx, by, '#fff',     8,  'normal');
    showFx(bx, by, '✨ +50', '#ffd54f');
  } else {
    spawnParticles(bx, by, '#66bb6a', 8, 'normal');
    spawnParticles(bx, by, '#fff',    4, 'normal');
    showFx(bx, by, '+' + f.pts, '#a5d6a7');
  }

  updateHud();
  if (state.score >= targetScore()) triggerLevelUp();
}

// ── SCREEN SHAKE ───────────────────────────────────────────────────

function screenShake() {
  const arena = document.getElementById('arena');
  arena.style.transition = 'transform 0s';
  let i = 0;
  const interval = setInterval(() => {
    const x = (Math.random() - 0.5) * 14;
    const y = (Math.random() - 0.5) * 10;
    arena.style.transform = `translate(${x}px,${y}px)`;
    if (++i > 8) { clearInterval(interval); arena.style.transform = ''; }
  }, 40);
}

// ── SHOW FX TEXT ───────────────────────────────────────────────────

function showFx(x, y, txt, color) {
  const el = document.createElement('div');
  el.className   = 'catch-fx';
  el.textContent = txt;
  el.style.left  = (x - 20) + 'px';
  el.style.top   = (y - 10) + 'px';
  el.style.color = color;
  document.getElementById('arena').appendChild(el);
  setTimeout(() => el.parentNode && el.parentNode.removeChild(el), 900);
}

function targetScore() { return state.level * 100; }

// ── TIMER ──────────────────────────────────────────────────────────

function tickTimer() {
  state.timer--;
  updateHud();
  if (state.timer <= 0) endGame();
}

// ── HUD UPDATE ─────────────────────────────────────────────────────

function updateHud() {
  document.getElementById('hud-lvl').textContent    = state.level;
  document.getElementById('hud-score').textContent  = state.score;
  document.getElementById('hud-target').textContent = targetScore();
  document.getElementById('hud-coins').textContent  = state.coins;

  const m  = Math.floor(state.timer / 60);
  const s  = state.timer % 60;
  const td = document.getElementById('hud-timer');
  td.textContent = '⏱ ' + m + ':' + (s < 10 ? '0' : '') + s;
  td.classList.toggle('timer-warn', state.timer <= 30);

  document.getElementById('house-side-emo').textContent = HOUSES[state.houseStage].e;
  document.getElementById('house-side-lbl').textContent = 'Stage ' + (state.houseStage + 1);

  const boughtBaskets = SHOP_ITEMS.filter(i => i.cat === 'basket' && state.owned[i.id]);
  document.getElementById('basket').textContent =
    boughtBaskets.length ? boughtBaskets[boughtBaskets.length - 1].e : '🧺';
}

// ── LEVEL UP ───────────────────────────────────────────────────────

function triggerLevelUp() {
  state.running = false;
  clearAll();
  fruits.forEach(f => f.el && f.el.parentNode && f.el.parentNode.removeChild(f.el));
  fruits = [];
  document.getElementById('lu-msg').textContent = 'You reached level ' + (state.level + 1) + '!';
  document.getElementById('levelup-overlay').classList.add('show');
  // Burst of gold particles
  if (pCanvas) {
    for (let i = 0; i < 5; i++) {
      setTimeout(() => {
        spawnParticles(arenaW * Math.random(), arenaH * 0.4, '#ffd54f', 12, 'normal');
        spawnParticles(arenaW * Math.random(), arenaH * 0.5, '#ff8f00', 10, 'normal');
        updateParticles();
      }, i * 120);
    }
  }
}

function nextLevel() {
  state.level++;
  state.score = 0;
  state.timer = 180;
  document.getElementById('levelup-overlay').classList.remove('show');
  state.running = true;
  lastT = 0;
  particles = [];
  timerInt = setInterval(tickTimer, 1000);
  scheduleSpawn();
  gameLoop = requestAnimationFrame(frame);
  updateHud();
}

// ── GAME OVER ──────────────────────────────────────────────────────

function endGame() {
  state.running = false;
  clearAll();
  fruits.forEach(f => f.el && f.el.parentNode && f.el.parentNode.removeChild(f.el));
  fruits = [];
  document.getElementById('go-level').textContent = state.level;
  document.getElementById('go-score').textContent  = state.score;
  document.getElementById('go-coins').textContent  = state.coins;
  showScreen('gameover');
}

// ── RESUME GAME ────────────────────────────────────────────────────

function resumeGame() {
  if (!state.running && state.timer > 0) {
    state.running = true;
    lastT = 0;
    timerInt = setInterval(tickTimer, 1000);
    scheduleSpawn();
    gameLoop = requestAnimationFrame(frame);
  }
  updateHud();
}

// ── SHOP ───────────────────────────────────────────────────────────

function openShop() {
  if (state.running) { state.running = false; clearAll(); }
  renderShop();
  showScreen('shop-screen');
}
function closeShop() { showScreen('gamescreen'); resumeGame(); }

function renderShop() {
  document.getElementById('shop-coins').textContent = state.coins;
  const g = document.getElementById('shop-grid');
  g.innerHTML = '';
  SHOP_ITEMS.forEach(item => {
    const owned  = !!state.owned[item.id];
    const canBuy = !owned && state.coins >= item.cost;
    const div = document.createElement('div');
    div.className = 'shop-item' + (owned ? ' owned' : '');
    div.innerHTML = `
      <div class="si-emo">${item.e}</div>
      <div class="si-name">${item.n}</div>
      <div class="si-cost">${item.cost} coins</div>
      <button class="si-btn" ${canBuy ? '' : 'disabled'} onclick="buyItem('${item.id}')">
        ${owned ? 'Owned ✓' : 'Buy'}
      </button>
    `;
    g.appendChild(div);
  });
}

function buyItem(id) {
  const item = SHOP_ITEMS.find(i => i.id === id);
  if (!item || state.owned[id] || state.coins < item.cost) return;
  state.coins -= item.cost;
  state.owned[id] = true;
  renderShop();
}

// ── HOUSE UPGRADE ──────────────────────────────────────────────────

function openUpgrade() {
  if (state.running) { state.running = false; clearAll(); }
  renderUpgrade();
  showScreen('upgrade-screen');
}
function closeUpgrade() { showScreen('gamescreen'); resumeGame(); }

function renderUpgrade() {
  const s = state.houseStage;
  const h = HOUSES[s];
  document.getElementById('upg-emo').textContent   = h.e;
  document.getElementById('upg-name').textContent  = h.n;
  document.getElementById('upg-stage').textContent = 'Stage ' + (s + 1) + ' / 20';
  document.getElementById('upg-bar').style.width   = ((s + 1) / 20 * 100) + '%';
  document.getElementById('upg-coins').textContent = 'Your Coins: ' + state.coins;

  const btn = document.getElementById('upg-btn');
  if (s < 19) {
    const next = HOUSES[s + 1];
    document.getElementById('upg-next').textContent =
      'Next: ' + next.n + ' (' + next.cost + ' coins)';
    btn.disabled    = state.coins < next.cost;
    btn.textContent = state.coins >= next.cost ? 'Upgrade! 🏗️' : 'Need ' + next.cost + ' coins';
    btn.style.display = '';
  } else {
    document.getElementById('upg-next').textContent = '🎉 Max level reached!';
    btn.style.display = 'none';
  }
}

function doUpgrade() {
  const s = state.houseStage;
  if (s >= 19) return;
  const cost = HOUSES[s + 1].cost;
  if (state.coins < cost) return;
  state.coins -= cost;
  state.houseStage++;
  renderUpgrade();
}

// ── KEYBOARD CONTROLS ──────────────────────────────────────────────

document.addEventListener('keydown', e => {
  if (e.key === 'ArrowLeft')  mouseX = Math.max(0, mouseX - 30);
  if (e.key === 'ArrowRight') mouseX = Math.min(arenaW, mouseX + 30);
});