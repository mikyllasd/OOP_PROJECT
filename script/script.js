// ── DATA ──────────────────────────────────────────────────────────

const HOUSES = [
  { n:"Old Shack",          e:"🏚️", cost:0    },
  { n:"Small House",        e:"🏠",  cost:100  },
  { n:"Cozy House",         e:"🏡",  cost:200  },
  { n:"Nice House",         e:"🏘️", cost:300  },
  { n:"Under Construction", e:"🏗️", cost:400  },
  { n:"Building",           e:"🏢",  cost:500  },
  { n:"Department Store",   e:"🏬",  cost:600  },
  { n:"Grand House",        e:"🏛️", cost:700  },
  { n:"Castle",             e:"🏰",  cost:800  },
  { n:"Palace",             e:"🏯",  cost:900  },
  { n:"Stadium",            e:"🏟️", cost:1000 },
  { n:"Beach House",        e:"🏖️", cost:1200 },
  { n:"Island House",       e:"🏝️", cost:1400 },
  { n:"Mountain House",     e:"🏞️", cost:1600 },
  { n:"City Penthouse",     e:"🌃",  cost:1800 },
  { n:"Skyline Villa",      e:"🌆",  cost:2000 },
  { n:"Sunset Mansion",     e:"🌇",  cost:2500 },
  { n:"Metro Tower",        e:"🏙️", cost:3000 },
  { n:"Luxury Hotel",       e:"🏩",  cost:4000 },
  { n:"Mansion with Pool",  e:"🏊",  cost:5000 }
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

const ACHIEVEMENTS = [
  { id:"first_harvest", icon:"🌾", name:"First Harvest",  desc:"Catch your first fruit"             },
  { id:"on_fire",       icon:"🔥", name:"On Fire",        desc:"Reach x3 combo"                     },
  { id:"untouchable",   icon:"🛡️", name:"Untouchable",    desc:"Let 5 bombs fall in one game"       },
  { id:"coin_hoarder",  icon:"💰", name:"Coin Hoarder",   desc:"Accumulate 500 coins total"         },
  { id:"dream_home",    icon:"🏰", name:"Dream Home",     desc:"Reach house stage 10"               },
  { id:"speed_farmer",  icon:"⚡", name:"Speed Farmer",   desc:"Reach level 5 with 90s remaining"  },
  { id:"perfect_level", icon:"⭐", name:"Perfect Level",  desc:"Complete a level with no bombs/rot" },
  { id:"legendary",     icon:"🏆", name:"Legendary",      desc:"Reach level 10"                     }
];

// ── STATE ─────────────────────────────────────────────────────────

let state = {
  char:"male", level:1, score:0, coins:0,
  timer:180, houseStage:0, owned:{}, running:false,
  combo:0, multiplier:1,
  bombsMissed:0, consecutiveMisses:0,
  perfectLevel:true, achUnlockedThisRun:0
};

let fruits = [], gameLoop = null, timerInt = null, spawnTO = null;
let arenaW = 0, arenaH = 0, mouseX = 200, smoothX = 200, prevSmoothX = 200;
let lastT = 0, farmerIdleTimer = 0;
let keys = {}, keyMoveInterval = null;
let isPaused = false, fromPause = false, isMuted = false;
let rainDrops = [];
let lastRunLevel = 1, lastRunScore = 0, lastRunCoins = 0;

// ── AUDIO ─────────────────────────────────────────────────────────

let audioCtx = null;

function getAudioCtx() {
  if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
  return audioCtx;
}

function playTone(freq, endFreq, duration, type, volume, distort) {
  if (isMuted) return;
  try {
    const ctx = getAudioCtx();
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.type = type || 'sine';
    osc.frequency.setValueAtTime(freq, ctx.currentTime);
    if (endFreq) osc.frequency.linearRampToValueAtTime(endFreq, ctx.currentTime + duration);
    gain.gain.setValueAtTime(volume || 0.3, ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + duration);
    if (distort) {
      const wave = ctx.createWaveShaper();
      const curve = new Float32Array(256);
      for (let i = 0; i < 256; i++) {
        const x = (i * 2) / 256 - 1;
        curve[i] = (Math.PI + 200) * x / (Math.PI + 200 * Math.abs(x));
      }
      wave.curve = curve;
      osc.connect(wave);
      wave.connect(gain);
    } else {
      osc.connect(gain);
    }
    gain.connect(ctx.destination);
    osc.start();
    osc.stop(ctx.currentTime + duration);
  } catch(e) {}
}

function soundCatch()   { playTone(520, 780, 0.08, 'sine', 0.25); }
function soundStar()    { playTone(880, null, 0.06, 'sine', 0.3); setTimeout(() => playTone(1100, null, 0.08, 'sine', 0.25), 80); }
function soundBomb()    { playTone(200, 80, 0.3, 'sawtooth', 0.35, true); }
function soundGameOver(){ playTone(400, 150, 0.8, 'sine', 0.3); }
function soundTick()    { playTone(800, 800, 0.04, 'square', 0.1); }

function soundLevelUp() {
  if (isMuted) return;
  [523, 659, 784, 1047].forEach((f, i) => setTimeout(() => playTone(f, null, 0.18, 'sine', 0.3), i * 100));
}

function soundCombo() {
  if (isMuted) return;
  [440, 554, 659].forEach((f, i) => setTimeout(() => playTone(f, null, 0.15, 'sine', 0.28), i * 80));
}

function toggleMute() {
  isMuted = !isMuted;
  localStorage.setItem('fc_muted', isMuted ? '1' : '0');
  document.getElementById('mute-btn').textContent = isMuted ? '🔇' : '🔊';
}

// ── LEADERBOARD ───────────────────────────────────────────────────

function getScores() {
  try { return JSON.parse(localStorage.getItem('fc_scores') || '[]'); } catch(e) { return []; }
}

function saveScore(name, score, level, coins) {
  const scores = getScores();
  scores.push({ name, score, level, coins, date: Date.now() });
  scores.sort((a, b) => b.score - a.score);
  scores.splice(10);
  localStorage.setItem('fc_scores', JSON.stringify(scores));
  return scores.findIndex(s => s.name === name && s.score === score && s.level === level) + 1;
}

function renderLeaderboard(highlightScore) {
  const scores = getScores();
  const el = document.getElementById('leaderboard-list');
  if (!scores.length) { el.innerHTML = '<p style="color:rgba(255,255,255,0.5);text-align:center">No scores yet!</p>'; return; }
  const medals = ['🥇','🥈','🥉'];
  const classes = ['gold','silver','bronze'];
  el.innerHTML = scores.map((s, i) => {
    const cls = i < 3 ? classes[i] : '';
    const mine = highlightScore && s.score === highlightScore ? ' mine' : '';
    return `<div class="lb-row ${cls}${mine}">
      <span class="lb-rank">${i < 3 ? medals[i] : (i+1)}</span>
      <span class="lb-name">${s.name || 'Unknown'}</span>
      <span class="lb-score">${s.score}</span>
      <span class="lb-meta">Lv${s.level} | 💰${s.coins}</span>
    </div>`;
  }).join('');
}

// ── ACHIEVEMENTS ──────────────────────────────────────────────────

function getUnlocked() {
  try { return JSON.parse(localStorage.getItem('fc_ach') || '[]'); } catch(e) { return []; }
}

function unlockAch(id) {
  const unlocked = getUnlocked();
  if (unlocked.includes(id)) return;
  unlocked.push(id);
  localStorage.setItem('fc_ach', JSON.stringify(unlocked));
  state.achUnlockedThisRun++;
  const ach = ACHIEVEMENTS.find(a => a.id === id);
  if (ach) showAchToast(ach);
}

function checkAchievements() {
  if (state.combo >= 3) unlockAch('on_fire');
  if (state.bombsMissed >= 5) unlockAch('untouchable');
  if (state.coins >= 500) unlockAch('coin_hoarder');
  if (state.houseStage >= 9) unlockAch('dream_home');
  if (state.level >= 5 && state.timer > 90) unlockAch('speed_farmer');
  if (state.level >= 10) unlockAch('legendary');
}

function showAchToast(ach) {
  const toast = document.getElementById('ach-toast');
  const item = document.createElement('div');
  item.className = 'ach-toast-item';
  item.innerHTML = `<span class="ach-toast-icon">${ach.icon}</span><div><div style="font-size:11px;color:rgba(255,213,79,0.8);margin-bottom:2px">ACHIEVEMENT UNLOCKED</div><div>${ach.name}</div></div>`;
  toast.appendChild(item);
  setTimeout(() => {
    item.classList.add('out');
    setTimeout(() => item.parentNode && item.parentNode.removeChild(item), 350);
  }, 3000);
}

function showAchievements() {
  if (state.running) { state.running = false; clearAll(); fromPause = false; }
  const unlocked = getUnlocked();
  const grid = document.getElementById('ach-grid');
  grid.innerHTML = ACHIEVEMENTS.map(a => {
    const ok = unlocked.includes(a.id);
    return `<div class="ach-card ${ok ? 'unlocked' : 'locked'}">
      <div class="ach-icon">${a.icon}</div>
      <div class="ach-name">${a.name}</div>
      <div class="ach-desc">${a.desc}</div>
    </div>`;
  }).join('');
  showScreen('achievements-screen');
}

function closeAchievements() {
  showScreen('gamescreen');
  resumeGame();
}

// ── STARS ─────────────────────────────────────────────────────────

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
      speed: Math.random() * 0.008 + 0.003,
      phase: Math.random() * Math.PI * 2
    }));
  }

  function drawStars(t) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    const nightLevel = state.level >= 10 ? 1 : 0;
    stars.forEach(s => {
      const base = nightLevel ? 0.5 : 0.3;
      const alpha = base + 0.7 * Math.abs(Math.sin(t * s.speed + s.phase));
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

// ── PARTICLES ─────────────────────────────────────────────────────

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
    const angle = (Math.PI * 2 / count) * i + Math.random() * 0.5;
    const speed = type === 'bomb' ? (4 + Math.random() * 5) : (2 + Math.random() * 3);
    const size  = type === 'bomb' ? (4 + Math.random() * 6) : (3 + Math.random() * 4);
    particles.push({
      x, y,
      vx: Math.cos(angle) * speed,
      vy: Math.sin(angle) * speed - (type === 'bomb' ? 0 : 2),
      color, size, life: 1,
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

  // Rain effect
  if (state.level >= 7 && state.level <= 9) {
    pCtx.strokeStyle = 'rgba(180,210,255,0.35)';
    pCtx.lineWidth = 1;
    rainDrops.forEach(d => {
      pCtx.beginPath();
      pCtx.moveTo(d.x, d.y);
      pCtx.lineTo(d.x - 3, d.y + 14);
      pCtx.stroke();
      d.x -= 1;
      d.y += 8;
      if (d.y > arenaH) { d.y = -20; d.x = Math.random() * arenaW; }
    });
  }

  // Fruit trails
  fruits.forEach(f => {
    if (!f.trail) return;
    const trailColors = { normal:'#66bb6a', star:'#ffd54f', bomb:'#8b0000', rotten:'#7cb342' };
    const tc = trailColors[f.type] || '#fff';
    f.trail.forEach((pt, i) => {
      const alpha = (i / f.trail.length) * 0.5 + 0.1;
      pCtx.globalAlpha = alpha;
      pCtx.beginPath();
      pCtx.arc(pt.x + 15, pt.y + 15, 5, 0, Math.PI * 2);
      pCtx.fillStyle = tc;
      pCtx.fill();
    });
    pCtx.globalAlpha = 1;
  });

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

// ── SCREEN TRANSITIONS ────────────────────────────────────────────

function showScreen(id) {
  const current = document.querySelector('.screen.active');
  const next = document.getElementById(id);
  if (current && current !== next) {
    current.style.opacity = '0';
    setTimeout(() => {
      current.classList.remove('active');
      current.style.opacity = '';
      next.style.opacity = '0';
      next.classList.add('active');
      requestAnimationFrame(() => { next.style.opacity = '1'; });
      if (id === 'leaderboard-screen') renderLeaderboard();
    }, 200);
  } else {
    next.classList.add('active');
  }
}

// ── WEATHER ───────────────────────────────────────────────────────

function updateWeather() {
  const sky = document.getElementById('sky-bg');
  const lvl = state.level;
  let gradient;

  if (lvl <= 3) {
    gradient = 'linear-gradient(180deg, #1a6b9a 0%, #2980b9 40%, #5dade2 70%, #85c1e9 100%)';
  } else if (lvl <= 6) {
    gradient = 'linear-gradient(180deg, #8B4513 0%, #d35400 30%, #e67e22 60%, #f0a500 100%)';
  } else if (lvl <= 9) {
    gradient = 'linear-gradient(180deg, #1a1a2e 0%, #2c3e50 40%, #34495e 70%, #4a6070 100%)';
  } else {
    gradient = 'linear-gradient(180deg, #000005 0%, #0a0a1a 40%, #0d1b2a 70%, #1a2a3a 100%)';
  }

  sky.style.background = gradient;

  // Rain drops init
  if (lvl >= 7 && lvl <= 9 && rainDrops.length === 0) {
    rainDrops = Array.from({ length: 80 }, () => ({
      x: Math.random() * arenaW,
      y: Math.random() * arenaH
    }));
  } else if ((lvl < 7 || lvl > 9) && rainDrops.length > 0) {
    rainDrops = [];
  }

  // Fruit opacity for rain
  document.querySelectorAll('.fruit').forEach(f => {
    f.style.opacity = (lvl >= 7 && lvl <= 9) ? '0.8' : '1';
  });
}

// ── CHAR SELECT ───────────────────────────────────────────────────

function selectChar(c) {
  state.char = c;
  document.getElementById('cc-male').classList.toggle('selected', c === 'male');
  document.getElementById('cc-female').classList.toggle('selected', c === 'female');
  document.getElementById('startBtn').disabled = false;
}

// ── GAME START / RETRY ────────────────────────────────────────────

function startGame() {
  state.level = 1; state.score = 0; state.timer = 180;
  state.owned = {}; state.coins = 0; state.houseStage = 0;
  state.combo = 0; state.multiplier = 1;
  state.bombsMissed = 0; state.consecutiveMisses = 0;
  state.perfectLevel = true; state.achUnlockedThisRun = 0;
  fruits = [];
  showScreen('gamescreen');
  setTimeout(initGame, 280);
}

function retryGame() {
  state.level = 1; state.score = 0; state.timer = 180;
  state.owned = {}; state.coins = 0; state.houseStage = 0;
  state.combo = 0; state.multiplier = 1;
  state.bombsMissed = 0; state.consecutiveMisses = 0;
  state.perfectLevel = true; state.achUnlockedThisRun = 0;
  fruits = [];
  showScreen('gamescreen');
  setTimeout(initGame, 280);
}

function saveScoreAndRetry() {
  const name = (document.getElementById('go-name-input').value || 'Player').trim();
  saveScore(name, lastRunScore, lastRunLevel, lastRunCoins);
  retryGame();
}

// ── GAME INIT ─────────────────────────────────────────────────────

function initGame() {
  const arena = document.getElementById('arena');
  arenaW = arena.offsetWidth;
  arenaH = arena.offsetHeight;
  mouseX = arenaW / 2;
  smoothX = mouseX;
  prevSmoothX = mouseX;

  document.getElementById('farmer').textContent = state.char === 'male' ? '👨‍🌾' : '👩‍🌾';

  clearAll();
  fruits.forEach(f => f.el && f.el.parentNode && f.el.parentNode.removeChild(f.el));
  fruits = [];
  particles = [];
  rainDrops = [];
  isPaused = false;

  document.getElementById('levelup-overlay').classList.remove('show');
  document.getElementById('pause-overlay').classList.remove('show');

  initParticles();
  updateWeather();
  state.running = true;
  updateHud();

  timerInt = setInterval(tickTimer, 1000);
  scheduleSpawn();
  lastT = performance.now();
  gameLoop = requestAnimationFrame(frame);

  arena.onmousemove = e => {
    const r = arena.getBoundingClientRect();
    mouseX = e.clientX - r.left;
    getAudioCtx();
  };
  arena.ontouchmove = e => {
    e.preventDefault();
    const r = arena.getBoundingClientRect();
    mouseX = e.touches[0].clientX - r.left;
    getAudioCtx();
  };
  arena.ontouchstart = e => {
    const r = arena.getBoundingClientRect();
    mouseX = e.touches[0].clientX - r.left;
    getAudioCtx();
    dismissTutorial();
  };
  arena.onclick = () => { getAudioCtx(); dismissTutorial(); };

  // Tutorial
  if (!localStorage.getItem('fc_hasPlayed')) {
    showTutorial();
  }

  // Mute restore
  isMuted = localStorage.getItem('fc_muted') === '1';
  document.getElementById('mute-btn').textContent = isMuted ? '🔇' : '🔊';

  // Resize observer
  window.addEventListener('resize', onResize);
}

function onResize() {
  const arena = document.getElementById('arena');
  arenaW = arena.offsetWidth;
  arenaH = arena.offsetHeight;
  resizeParticleCanvas();
}

// ── TUTORIAL ──────────────────────────────────────────────────────

let tutTimeout = null;

function showTutorial() {
  const overlay = document.getElementById('tutorial-overlay');
  const hand = document.getElementById('tutorial-hand');
  overlay.classList.add('show');

  function positionHand() {
    const bx = smoothX;
    const by = arenaH - (60 + 48 + 38);
    hand.style.left = bx + 'px';
    hand.style.top  = (by - 60) + 'px';
    const lbl = document.getElementById('tut-basket-label');
    lbl.style.left = (bx - 80) + 'px';
    lbl.style.top  = (by - 90) + 'px';
  }
  positionHand();
  tutTimeout = setTimeout(dismissTutorial, 4000);
}

function dismissTutorial() {
  const overlay = document.getElementById('tutorial-overlay');
  overlay.classList.remove('show');
  localStorage.setItem('fc_hasPlayed', '1');
  if (tutTimeout) { clearTimeout(tutTimeout); tutTimeout = null; }
}

// ── PAUSE ─────────────────────────────────────────────────────────

function pauseGame() {
  if (!state.running) return;
  isPaused = true;
  state.running = false;
  clearAll();
  document.getElementById('pause-lvl').textContent   = state.level;
  document.getElementById('pause-score').textContent = state.score;
  document.getElementById('pause-coins').textContent = state.coins;
  const m = Math.floor(state.timer / 60), s = state.timer % 60;
  document.getElementById('pause-timer').textContent = m + ':' + (s < 10 ? '0' : '') + s;
  document.getElementById('pause-overlay').classList.add('show');
}

function resumeFromPause() {
  document.getElementById('pause-overlay').classList.remove('show');
  isPaused = false;
  state.running = true;
  lastT = performance.now();
  timerInt = setInterval(tickTimer, 1000);
  scheduleSpawn();
  gameLoop = requestAnimationFrame(frame);
}

function openShopFromPause() {
  document.getElementById('pause-overlay').classList.remove('show');
  fromPause = true;
  renderShop();
  showScreen('shop-screen');
}

function openUpgradeFromPause() {
  document.getElementById('pause-overlay').classList.remove('show');
  fromPause = true;
  renderUpgrade();
  showScreen('upgrade-screen');
}

// ── CLEAR ALL ─────────────────────────────────────────────────────

function clearAll() {
  if (timerInt) { clearInterval(timerInt);       timerInt = null; }
  if (spawnTO)  { clearTimeout(spawnTO);          spawnTO  = null; }
  if (gameLoop) { cancelAnimationFrame(gameLoop); gameLoop = null; }
  if (keyMoveInterval) { clearInterval(keyMoveInterval); keyMoveInterval = null; }
}

// ── FRUIT SPAWNING ────────────────────────────────────────────────

function scheduleSpawn() {
  const base  = Math.max(400, 1800 - state.level * 80);
  const delay = base + Math.random() * 600;
  spawnTO = setTimeout(() => {
    if (state.running) { spawnFruit(); scheduleSpawn(); }
  }, delay);
}

function spawnFruit() {
  const total = FRUITS.reduce((a, f) => a + f.w, 0);
  let r = Math.random() * total, chosen = FRUITS[0];
  for (const f of FRUITS) { r -= f.w; if (r <= 0) { chosen = f; break; } }

  const el = document.createElement('div');
  el.className = 'fruit';
  el.textContent = chosen.e;

  const x = 20 + Math.random() * (arenaW - 60);
  el.style.left = x + 'px';
  el.style.top  = '-44px';
  el.style.animation = `fruitWobble ${0.6 + Math.random() * 0.6}s ease-in-out infinite alternate`;

  if (state.level >= 7 && state.level <= 9) el.style.opacity = '0.8';
  if (state.level >= 10) el.style.filter = 'drop-shadow(0 0 6px rgba(255,255,200,0.5))';

  document.getElementById('arena').appendChild(el);
  const speed = 1.8 + state.level * 0.35 + Math.random() * 1.2;
  const drift = (Math.random() - 0.5) * 0.6 * Math.min(state.level, 6);
  fruits.push({ el, x, y: -44, pts: chosen.pts, speed, type: chosen.type, drift, trail: [] });
}

// ── GAME LOOP ─────────────────────────────────────────────────────

function frame(t) {
  if (!state.running) return;
  const dt = Math.min(t - lastT, 50);
  lastT = t;

  // Keyboard held movement
  if (keys['ArrowLeft'])  mouseX = Math.max(0, mouseX - 4);
  if (keys['ArrowRight']) mouseX = Math.min(arenaW, mouseX + 4);

  prevSmoothX = smoothX;
  smoothX += (mouseX - smoothX) * 0.16;
  const bx = Math.max(30, Math.min(arenaW - 30, smoothX));

  const farmer = document.getElementById('farmer');
  const basket = document.getElementById('basket');

  // Farmer lean animation
  const dx = smoothX - prevSmoothX;
  if (Math.abs(dx) > 0.5) {
    farmerIdleTimer = 0;
    const tiltDir = dx < 0 ? -1 : 1;
    farmer.style.transform = `translateX(-50%) rotate(${tiltDir * 8}deg) scaleX(${dx > 0 ? -1 : 1})`;
  } else {
    farmerIdleTimer += dt;
    if (farmerIdleTimer > 500) {
      farmer.style.transform = 'translateX(-50%) rotate(0deg) scaleX(1)';
    }
  }

  farmer.style.left   = bx + 'px';
  basket.style.left   = bx + 'px';
  basket.style.bottom = (60 + 48) + 'px';

  const catchZone = Math.max(36, arenaW * 0.06);
  const catchY = arenaH - (60 + 48 + 38);

  for (let i = fruits.length - 1; i >= 0; i--) {
    const f = fruits[i];
    f.y += f.speed * (dt / 16);
    f.x += f.drift;

    // Clamp x
    if (f.x < 0) { f.x = 0; f.drift = Math.abs(f.drift); }
    if (f.x > arenaW - 40) { f.x = arenaW - 40; f.drift = -Math.abs(f.drift); }

    f.el.style.top  = f.y + 'px';
    f.el.style.left = f.x + 'px';

    // Trail
    f.trail.push({ x: f.x, y: f.y });
    if (f.trail.length > 6) f.trail.shift();

    const fx = f.x + 15;
    const fy = f.y + 15;

    if (Math.abs(fx - bx) < catchZone && fy >= catchY - 10 && fy <= catchY + 44) {
      collectFruit(f, bx, catchY);
      fruits.splice(i, 1);
    } else if (f.y > arenaH) {
      handleMiss(f);
      fruits.splice(i, 1);
    }
  }

  updateParticles();
  gameLoop = requestAnimationFrame(frame);
}

// ── HANDLE MISS ───────────────────────────────────────────────────

function handleMiss(f) {
  if (f.el.parentNode) f.el.parentNode.removeChild(f.el);

  if (f.type === 'bomb') {
    // Near-miss green glow reward
    state.bombsMissed++;
    const arena = document.getElementById('arena');
    arena.classList.remove('near-miss-glow');
    void arena.offsetWidth;
    arena.classList.add('near-miss-glow');
    setTimeout(() => arena.classList.remove('near-miss-glow'), 600);
    checkAchievements();
  } else if (f.type === 'normal' || f.type === 'star') {
    // Miss puff
    spawnParticles(f.x + 15, arenaH - 64, '#888', 5, 'normal');
    showFx(f.x + 15, arenaH - 80, '✕', '#888');
    // Reset combo
    state.combo = 0;
    state.multiplier = 1;
    document.getElementById('hud-combo').style.display = 'none';
    state.consecutiveMisses++;
    if (state.consecutiveMisses >= 3) {
      const w = document.getElementById('miss-warning');
      w.textContent = '⚠ Too many misses!';
      w.classList.remove('show');
      void w.offsetWidth;
      w.classList.add('show');
      state.consecutiveMisses = 0;
    }
  }
}

// ── COLLECT FRUIT ─────────────────────────────────────────────────

function collectFruit(f, bx, by) {
  if (f.el.parentNode) f.el.parentNode.removeChild(f.el);

  if (f.type === 'bomb') {
    state.perfectLevel = false;
    state.combo = 0;
    state.multiplier = 1;
    document.getElementById('hud-combo').style.display = 'none';
    state.score = Math.max(0, state.score + f.pts);
    spawnShockwave(bx, by);
    spawnParticles(bx, by, '#ff6f00', 18, 'bomb');
    spawnParticles(bx, by, '#ffd54f', 12, 'bomb');
    spawnParticles(bx, by, '#ef5350', 10, 'bomb');
    screenShake();
    showFx(bx, by, '💥 -30', '#ef5350');
    soundBomb();
    animFarmerShake();
  } else if (f.type === 'rotten') {
    state.perfectLevel = false;
    state.combo = 0;
    state.multiplier = 1;
    document.getElementById('hud-combo').style.display = 'none';
    state.score = Math.max(0, state.score + f.pts);
    spawnParticles(bx, by, '#8bc34a', 10, 'normal');
    spawnParticles(bx, by, '#33691e', 8,  'normal');
    showFx(bx, by, '🤢 -20', '#aed581');
  } else if (f.type === 'star') {
    state.consecutiveMisses = 0;
    state.combo++;
    updateCombo();
    const earned = Math.ceil(f.pts / 5) * state.multiplier;
    state.score = Math.max(0, state.score + f.pts * state.multiplier);
    state.coins += earned;
    spawnParticles(bx, by, '#ffd54f', 16, 'normal');
    spawnParticles(bx, by, '#fff',     8,  'normal');
    showFx(bx, by, '✨ +' + (f.pts * state.multiplier), '#ffd54f');
    soundStar();
    animFarmerJump();
    unlockAch('first_harvest');
  } else {
    state.consecutiveMisses = 0;
    state.combo++;
    updateCombo();
    const earned = Math.ceil(f.pts / 5) * state.multiplier;
    state.score = Math.max(0, state.score + f.pts * state.multiplier);
    state.coins += earned;
    spawnParticles(bx, by, '#66bb6a', 8, 'normal');
    spawnParticles(bx, by, '#fff',    4, 'normal');
    showFx(bx, by, '+' + (f.pts * state.multiplier), '#a5d6a7');
    soundCatch();
    animFarmerJump();
    unlockAch('first_harvest');
  }

  checkAchievements();
  updateHud();
  if (state.score >= targetScore()) triggerLevelUp();
}

// ── COMBO ─────────────────────────────────────────────────────────

function updateCombo() {
  const prev = state.multiplier;
  if      (state.combo >= 11) state.multiplier = 5;
  else if (state.combo >= 8)  state.multiplier = 4;
  else if (state.combo >= 5)  state.multiplier = 3;
  else if (state.combo >= 3)  state.multiplier = 2;
  else                        state.multiplier = 1;

  if (state.multiplier > 1) {
    const el = document.getElementById('hud-combo');
    el.style.display = 'flex';
    document.getElementById('hud-multi').textContent = state.multiplier;
  }

  if (state.multiplier > prev) {
    const labels = { 2:'🔥 x2', 3:'⚡ x3', 4:'💥 x4', 5:'🌟 x5' };
    flashCombo(labels[state.multiplier] || '🔥 x' + state.multiplier);
    soundCombo();
    if (state.combo >= 3) unlockAch('on_fire');
  }
}

function flashCombo(txt) {
  const el = document.getElementById('combo-flash');
  el.textContent = txt;
  el.classList.remove('pop');
  void el.offsetWidth;
  el.classList.add('pop');
}

// ── FARMER ANIMATIONS ─────────────────────────────────────────────

function animFarmerJump() {
  const f = document.getElementById('farmer');
  f.style.animation = 'none';
  void f.offsetWidth;
  f.style.animation = 'farmerJump 0.35s ease-out';
  setTimeout(() => { f.style.animation = ''; }, 400);
}

function animFarmerShake() {
  const f = document.getElementById('farmer');
  f.style.animation = 'none';
  void f.offsetWidth;
  f.style.animation = 'farmerShake 0.4s ease-out';
  setTimeout(() => { f.style.animation = ''; }, 450);
}

// ── SCREEN SHAKE ──────────────────────────────────────────────────

function screenShake() {
  const arena = document.getElementById('arena');
  arena.style.transition = 'transform 0s';
  let i = 0;
  const iv = setInterval(() => {
    const x = (Math.random() - 0.5) * 14;
    const y = (Math.random() - 0.5) * 10;
    arena.style.transform = `translate(${x}px,${y}px)`;
    if (++i > 8) { clearInterval(iv); arena.style.transform = ''; }
  }, 40);
}

// ── FX TEXT ───────────────────────────────────────────────────────

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

function targetScore() {
  return state.level * 100 + (state.level - 1) * 50;
}

// ── TIMER ─────────────────────────────────────────────────────────

function tickTimer() {
  state.timer--;
  if (state.timer === 30 || (state.timer <= 30 && state.timer > 0)) {
    soundTick();
  }
  updateHud();
  if (state.timer <= 0) endGame();
}

// ── HUD ───────────────────────────────────────────────────────────

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

// ── LEVEL UP ──────────────────────────────────────────────────────

function triggerLevelUp() {
  state.running = false;
  clearAll();
  fruits.forEach(f => f.el && f.el.parentNode && f.el.parentNode.removeChild(f.el));
  fruits = [];

  if (state.perfectLevel) unlockAch('perfect_level');

  document.getElementById('lu-msg').textContent = 'You reached level ' + (state.level + 1) + '!';
  document.getElementById('levelup-overlay').classList.add('show');
  soundLevelUp();

  for (let i = 0; i < 5; i++) {
    setTimeout(() => {
      spawnParticles(arenaW * Math.random(), arenaH * 0.4, '#ffd54f', 12, 'normal');
      spawnParticles(arenaW * Math.random(), arenaH * 0.5, '#ff8f00', 10, 'normal');
      updateParticles();
    }, i * 120);
  }
}

function nextLevel() {
  state.level++;
  state.score = 0;
  state.timer = 180;
  state.perfectLevel = true;
  document.getElementById('levelup-overlay').classList.remove('show');
  state.running = true;
  lastT = performance.now();
  particles = [];
  updateWeather();
  timerInt = setInterval(tickTimer, 1000);
  scheduleSpawn();
  gameLoop = requestAnimationFrame(frame);
  updateHud();
  checkAchievements();
}

// ── GAME OVER ─────────────────────────────────────────────────────

function endGame() {
  state.running = false;
  clearAll();
  fruits.forEach(f => f.el && f.el.parentNode && f.el.parentNode.removeChild(f.el));
  fruits = [];

  lastRunLevel = state.level;
  lastRunScore = state.score;
  lastRunCoins = state.coins;

  document.getElementById('go-level').textContent = state.level;
  document.getElementById('go-score').textContent  = state.score;
  document.getElementById('go-coins').textContent  = state.coins;

  // Icon & message by level
  const goIcon  = document.getElementById('go-icon');
  const goTitle = document.getElementById('go-title');
  const goSub   = document.getElementById('go-subtitle');
  if (state.level >= 10) {
    goIcon.textContent  = '🏆';
    goTitle.textContent = 'Legendary!';
    goTitle.classList.add('gold');
    goSub.textContent   = 'You are an absolute legend!';
    document.getElementById('go-new-record').style.display = 'block';
  } else if (state.level >= 7) {
    goIcon.textContent  = '😎';
    goTitle.textContent = 'Impressive Run!';
    goTitle.classList.remove('gold');
    goSub.textContent   = 'That was seriously good!';
  } else if (state.level >= 4) {
    goIcon.textContent  = '😤';
    goTitle.textContent = 'Not Bad!';
    goTitle.classList.remove('gold');
    goSub.textContent   = 'Keep trying, you\'re getting there!';
  } else {
    goIcon.textContent  = '😢';
    goTitle.textContent = 'Game Over!';
    goTitle.classList.remove('gold');
    goSub.textContent   = '';
    document.getElementById('go-new-record').style.display = 'none';
  }

  // Check new record
  const scores = getScores();
  const isNewRecord = scores.length === 0 || state.score > scores[0].score;
  if (isNewRecord && state.level < 10) {
    document.getElementById('go-new-record').style.display = 'block';
  }

  // Show rank stat
  const rankStat = document.getElementById('go-rank-stat');
  rankStat.style.display = '';

  // Show achievements unlocked
  if (state.achUnlockedThisRun > 0) {
    const achStat = document.getElementById('go-ach-stat');
    achStat.style.display = '';
    document.getElementById('go-ach-count').textContent = state.achUnlockedThisRun;
  }

  soundGameOver();
  showScreen('gameover');
}

// ── RESUME GAME ───────────────────────────────────────────────────

function resumeGame() {
  if (!state.running && state.timer > 0 && !isPaused) {
    state.running = true;
    lastT = performance.now();
    timerInt = setInterval(tickTimer, 1000);
    scheduleSpawn();
    gameLoop = requestAnimationFrame(frame);
  }
  updateHud();
}

// ── SHOP ──────────────────────────────────────────────────────────

function openShop() {
  if (state.running) { state.running = false; clearAll(); }
  fromPause = false;
  renderShop();
  showScreen('shop-screen');
}

function closeShop() {
  if (fromPause) {
    fromPause = false;
    showScreen('gamescreen');
    document.getElementById('pause-overlay').classList.add('show');
  } else {
    showScreen('gamescreen');
    resumeGame();
  }
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
      </button>`;
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

// ── HOUSE UPGRADE ─────────────────────────────────────────────────

function openUpgrade() {
  if (state.running) { state.running = false; clearAll(); }
  fromPause = false;
  renderUpgrade();
  showScreen('upgrade-screen');
}

function closeUpgrade() {
  if (fromPause) {
    fromPause = false;
    showScreen('gamescreen');
    document.getElementById('pause-overlay').classList.add('show');
  } else {
    showScreen('gamescreen');
    resumeGame();
  }
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

  // Slide out old
  const emoEl = document.getElementById('upg-emo');
  emoEl.style.transition = 'transform 0.4s ease, opacity 0.4s ease';
  emoEl.style.transform  = 'translateX(-120px)';
  emoEl.style.opacity    = '0';

  setTimeout(() => {
    state.houseStage++;
    renderUpgrade();
    const newEmo = document.getElementById('upg-emo');
    newEmo.style.transition = 'none';
    newEmo.style.transform  = 'translateX(120px)';
    newEmo.style.opacity    = '0';
    void newEmo.offsetWidth;
    newEmo.style.transition = 'transform 0.4s ease, opacity 0.4s ease';
    newEmo.style.transform  = 'translateX(0)';
    newEmo.style.opacity    = '1';

    // Announce house name in arena
    const ann = document.getElementById('house-announce');
    ann.textContent = HOUSES[state.houseStage].e + ' ' + HOUSES[state.houseStage].n;
    ann.classList.remove('show');
    void ann.offsetWidth;
    ann.classList.add('show');

    // Gold particles in arena
    if (pCanvas) {
      for (let i = 0; i < 4; i++) {
        setTimeout(() => {
          spawnParticles(arenaW - 60, 60, '#ffd54f', 5, 'normal');
          updateParticles();
        }, i * 80);
      }
    }

    // Milestone sound
    const milestones = [4, 9, 14, 19];
    if (milestones.includes(state.houseStage)) soundLevelUp();

    checkAchievements();
  }, 420);
}

// ── KEYBOARD CONTROLS ─────────────────────────────────────────────

document.addEventListener('keydown', e => {
  if (e.key === 'Escape') {
    if (state.running) pauseGame();
    else if (isPaused) resumeFromPause();
    return;
  }
  if (e.key === 'ArrowLeft' || e.key === 'ArrowRight') {
    e.preventDefault();
    keys[e.key] = true;
    getAudioCtx();
  }
});

document.addEventListener('keyup', e => {
  keys[e.key] = false;
});

// Auto-pause on tab/window hide
document.addEventListener('visibilitychange', () => {
  if (document.hidden && state.running) pauseGame();
});