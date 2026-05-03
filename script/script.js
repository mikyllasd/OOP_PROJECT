const HOUSES = [
  { n: "Old Shack",           e: "🏚️", cost: 0    },
  { n: "Small House",         e: "🏠",  cost: 100  },
  { n: "Cozy House",          e: "🏡",  cost: 200  },
  { n: "Nice House",          e: "🏘️", cost: 300  },
  { n: "Under Construction",  e: "🏗️", cost: 400  },
  { n: "Building",            e: "🏢",  cost: 500  },
  { n: "Department Store",    e: "🏬",  cost: 600  },
  { n: "Grand House",         e: "🏛️", cost: 700  },
  { n: "Castle",              e: "🏰",  cost: 800  },
  { n: "Palace",              e: "🏯",  cost: 900  },
  { n: "Stadium House",       e: "🏟️", cost: 1000 },
  { n: "Beach House",         e: "🏖️", cost: 1200 },
  { n: "Island House",        e: "🏝️", cost: 1400 },
  { n: "Mountain House",      e: "🏞️", cost: 1600 },
  { n: "City Penthouse",      e: "🌃",  cost: 1800 },
  { n: "Skyline Villa",       e: "🌆",  cost: 2000 },
  { n: "Sunset Mansion",      e: "🌇",  cost: 2500 },
  { n: "Metropolitan Tower",  e: "🏙️", cost: 3000 },
  { n: "Luxury Hotel",        e: "🏩",  cost: 4000 },
  { n: "Mansion with Pool",   e: "🏊‍♂️", cost: 5000 }
];

const SHOP_ITEMS = [
  { id: "hat",      cat: "clothes", n: "Straw Hat",    e: "👒", cost: 50  },
  { id: "cap",      cat: "clothes", n: "Cool Cap",      e: "🧢", cost: 75  },
  { id: "shirt",    cat: "clothes", n: "Red Shirt",     e: "👕", cost: 100 },
  { id: "overalls", cat: "clothes", n: "Blue Overalls", e: "👖", cost: 120 },
  { id: "woven",    cat: "basket",  n: "Woven Basket",  e: "🧺", cost: 80  },
  { id: "bucket",   cat: "basket",  n: "Metal Bucket",  e: "🪣", cost: 100 },
  { id: "golden",   cat: "basket",  n: "Golden Basket", e: "🎁", cost: 200 }
];

const FRUITS = [
  { e: "🍎", pts: 10,  w: 40 },
  { e: "🍌", pts: 10,  w: 40 },
  { e: "✨", pts: 50,  w: 5  },
  { e: "🍇", pts: 10,  w: 30 },
  { e: "🍊", pts: 10,  w: 35 },
  { e: "🤢", pts: -20, w: 15 }
];

let state = {
  char: "male",
  level: 1,
  score: 0,
  coins: 0,
  timer: 180,
  houseStage: 0,
  owned: {},
  running: false
};

let fruits = [];
let gameLoop = null;
let timerInt = null;
let spawnTO = null;
let arenaW = 0;
let arenaH = 0;
let mouseX = 200;
let smoothX = 200;
let lastT = 0;

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
  state.level = 1;
  state.score = 0;
  state.timer = 180;
  state.owned = {};
  state.coins = 0;
  state.houseStage = 0;
  fruits = [];
  showScreen('gamescreen');
  setTimeout(initGame, 80);
}

function retryGame() {
  state.level = 1;
  state.score = 0;
  state.timer = 180;
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
  fruits.forEach(f => { if (f.el && f.el.parentNode) f.el.parentNode.removeChild(f.el); });
  fruits = [];

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
  if (timerInt) { clearInterval(timerInt); timerInt = null; }
  if (spawnTO)  { clearTimeout(spawnTO);   spawnTO  = null; }
  if (gameLoop) { cancelAnimationFrame(gameLoop); gameLoop = null; }
}

// ── FRUIT SPAWNING ─────────────────────────────────────────────────

function scheduleSpawn() {
  const base  = Math.max(500, 1800 - state.level * 80);
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
  el.style.top  = '-40px';
  document.getElementById('arena').appendChild(el);

  const speed = 1.8 + state.level * 0.35 + Math.random() * 1.2;
  fruits.push({ el, x, y: -40, pts: chosen.pts, speed });
}

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
      if (f.el.parentNode) f.el.parentNode.removeChild(f.el);
      fruits.splice(i, 1);
    }
  }

  gameLoop = requestAnimationFrame(frame);
}

// ── COLLECT FRUIT ──────────────────────────────────────────────────

function collectFruit(f, bx, by) {
  if (f.el.parentNode) f.el.parentNode.removeChild(f.el);

  state.score = Math.max(0, state.score + f.pts);
  if (f.pts > 0) state.coins += Math.ceil(f.pts / 5);

  const color = f.pts >= 50 ? '#f9a825' : f.pts > 0 ? '#4caf50' : '#e74c3c';
  showFx(bx, by, (f.pts > 0 ? '+' : '') + f.pts, color);
  updateHud();

  if (state.score >= targetScore()) triggerLevelUp();
}

function showFx(x, y, txt, color) {
  const el = document.createElement('div');
  el.className = 'catch-fx';
  el.textContent = txt;
  el.style.left  = x + 'px';
  el.style.top   = y + 'px';
  el.style.color = color;
  el.style.position = 'absolute';
  document.getElementById('arena').appendChild(el);
  setTimeout(() => { if (el.parentNode) el.parentNode.removeChild(el); }, 850);
}

function targetScore() {
  return state.level * 100;
}

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
  fruits.forEach(f => { if (f.el && f.el.parentNode) f.el.parentNode.removeChild(f.el); });
  fruits = [];
  document.getElementById('lu-msg').textContent = 'You reached level ' + (state.level + 1) + '!';
  document.getElementById('levelup-overlay').classList.add('show');
}

function nextLevel() {
  state.level++;
  state.score = 0;
  state.timer = 180;
  document.getElementById('levelup-overlay').classList.remove('show');
  state.running = true;
  lastT = 0;
  timerInt = setInterval(tickTimer, 1000);
  scheduleSpawn();
  gameLoop = requestAnimationFrame(frame);
  updateHud();
}

// ── GAME OVER ──────────────────────────────────────────────────────

function endGame() {
  state.running = false;
  clearAll();
  fruits.forEach(f => { if (f.el && f.el.parentNode) f.el.parentNode.removeChild(f.el); });
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

function closeShop() {
  showScreen('gamescreen');
  resumeGame();
}

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

function closeUpgrade() {
  showScreen('gamescreen');
  resumeGame();
}

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
    document.getElementById('upg-next').textContent = 'Next: ' + next.n + ' (' + next.cost + ' coins)';
    btn.disabled    = state.coins < next.cost;
    btn.textContent = state.coins >= next.cost ? 'Upgrade!' : 'Need ' + next.cost + ' coins';
    btn.style.display = '';
  } else {
    document.getElementById('upg-next').textContent = 'Max level reached! 🎉';
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