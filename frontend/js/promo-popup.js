// ==========================================
// PROMO POPUP — Auto-shows on user pages
// ==========================================

(function () {
  'use strict';

  // Don't show if already shown today
  const lastShown = localStorage.getItem('promoPopupDate');
  const today = new Date().toDateString();
  if (lastShown === today) return;

  // Inject CSS
  const style = document.createElement('style');
  style.textContent = `
    .promo-popup-overlay {
      position: fixed;
      inset: 0;
      background: rgba(7, 21, 33, 0.85);
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      z-index: 99999;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 20px;
      opacity: 0;
      pointer-events: none;
      transition: opacity 0.4s ease;
    }

    .promo-popup-overlay.active {
      opacity: 1;
      pointer-events: auto;
    }

    .promo-popup-card {
      background: #ffffff;
      border-radius: 24px;
      max-width: 560px;
      width: 100%;
      max-height: 90vh;
      overflow-y: auto;
      position: relative;
      transform: scale(0.9) translateY(20px);
      transition: transform 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
      box-shadow: 0 30px 80px rgba(0, 0, 0, 0.35);
    }

    .promo-popup-overlay.active .promo-popup-card {
      transform: scale(1) translateY(0);
    }

    .promo-popup-card::-webkit-scrollbar {
      width: 6px;
    }

    .promo-popup-card::-webkit-scrollbar-thumb {
      background: #cbd5e1;
      border-radius: 6px;
    }

    .promo-popup-close {
      position: absolute;
      top: 16px;
      right: 16px;
      width: 38px;
      height: 38px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: white;
      font-size: 22px;
      font-weight: 300;
      line-height: 1;
      cursor: pointer;
      z-index: 10;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.25s ease;
    }

    .promo-popup-close:hover {
      background: rgba(255, 255, 255, 0.35);
      transform: rotate(90deg);
    }

    .promo-popup-header {
      background: linear-gradient(135deg, #0B1F33 0%, #102A43 60%, #1a3a5c 100%);
      padding: 40px 32px 32px;
      text-align: center;
      position: relative;
      overflow: hidden;
    }

    .promo-popup-header::before {
      content: "";
      position: absolute;
      top: -60px;
      right: -60px;
      width: 200px;
      height: 200px;
      background: rgba(199, 164, 106, 0.15);
      border-radius: 50%;
    }

    .promo-popup-header::after {
      content: "";
      position: absolute;
      bottom: -40px;
      left: -40px;
      width: 140px;
      height: 140px;
      background: rgba(30, 138, 138, 0.15);
      border-radius: 50%;
    }

    .promo-popup-icon {
      font-size: 56px;
      margin-bottom: 12px;
      position: relative;
      z-index: 2;
      animation: promoBounce 1.5s ease infinite;
    }

    @keyframes promoBounce {
      0%, 100% { transform: translateY(0); }
      50% { transform: translateY(-8px); }
    }

    .promo-popup-title {
      font-family: "Playfair Display", serif;
      font-size: 32px;
      font-weight: 700;
      color: #ffffff;
      margin: 0 0 6px;
      position: relative;
      z-index: 2;
    }

    .promo-popup-title span {
      color: #C7A46A;
    }

    .promo-popup-subtitle {
      color: #94a3b8;
      font-size: 14px;
      margin: 0;
      position: relative;
      z-index: 2;
    }

    .promo-popup-body {
      padding: 24px 24px 16px;
      background: #F8F6F1;
    }

    .promo-popup-loading {
      text-align: center;
      padding: 40px 20px;
      color: #64748b;
      font-size: 14px;
    }

    .promo-popup-loading::before {
      content: "";
      display: inline-block;
      width: 32px;
      height: 32px;
      border: 3px solid rgba(199, 164, 106, 0.25);
      border-top-color: #C7A46A;
      border-radius: 50%;
      animation: promoSpin 0.8s linear infinite;
      margin-bottom: 12px;
    }

    @keyframes promoSpin {
      to { transform: rotate(360deg); }
    }

    .promo-popup-item {
      background: #ffffff;
      border: 1px solid #e9eef4;
      border-radius: 16px;
      padding: 18px;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 16px;
      transition: all 0.25s ease;
      position: relative;
      overflow: hidden;
    }

    .promo-popup-item::before {
      content: "";
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: 4px;
      background: linear-gradient(180deg, #C7A46A, #aa884e);
    }

    .promo-popup-item:hover {
      transform: translateX(4px);
      box-shadow: 0 10px 30px rgba(11, 31, 51, 0.1);
    }

    .promo-popup-item-left {
      flex: 1;
      min-width: 0;
    }

    .promo-popup-code {
      display: inline-block;
      font-family: 'Courier New', monospace;
      background: #F8F6F1;
      border: 2px dashed #C7A46A;
      color: #0B1F33;
      padding: 6px 14px;
      border-radius: 8px;
      font-weight: 800;
      font-size: 15px;
      letter-spacing: 1px;
      margin-bottom: 6px;
    }

    .promo-popup-desc {
      font-size: 12px;
      color: #64748b;
      line-height: 1.4;
    }

    .promo-popup-item-right {
      text-align: right;
      flex-shrink: 0;
    }

    .promo-popup-discount {
      font-family: "Playfair Display", serif;
      font-size: 24px;
      font-weight: 700;
      color: #16a34a;
      line-height: 1;
      margin-bottom: 6px;
    }

    .promo-popup-copy {
      background: #0B1F33;
      color: #ffffff;
      border: none;
      padding: 7px 16px;
      border-radius: 8px;
      font-size: 11px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      cursor: pointer;
      transition: all 0.2s ease;
    }

    .promo-popup-copy:hover {
      background: #C7A46A;
      transform: translateY(-1px);
    }

    .promo-popup-footer {
      padding: 16px 24px 24px;
      text-align: center;
      background: #F8F6F1;
    }

    .promo-popup-dismiss {
      background: transparent;
      color: #94a3b8;
      border: none;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      padding: 8px 16px;
      border-radius: 8px;
      transition: all 0.2s ease;
    }

    .promo-popup-dismiss:hover {
      color: #0B1F33;
      background: rgba(0, 0, 0, 0.04);
    }

    .promo-popup-toast {
      position: fixed;
      bottom: 30px;
      left: 50%;
      transform: translateX(-50%) translateY(100px);
      background: #16a34a;
      color: white;
      padding: 14px 24px;
      border-radius: 12px;
      font-weight: 600;
      font-size: 14px;
      z-index: 100000;
      box-shadow: 0 10px 30px rgba(22, 163, 74, 0.4);
      animation: promoToastSlide 0.3s ease forwards;
    }

    @keyframes promoToastSlide {
      to { transform: translateX(-50%) translateY(0); }
    }

    @media (max-width: 640px) {
      .promo-popup-card {
        border-radius: 20px;
      }

      .promo-popup-header {
        padding: 32px 20px 24px;
      }

      .promo-popup-title {
        font-size: 24px;
      }

      .promo-popup-icon {
        font-size: 44px;
      }

      .promo-popup-body {
        padding: 16px 16px 8px;
      }

      .promo-popup-item {
        padding: 14px;
      }

      .promo-popup-code {
        font-size: 13px;
        padding: 5px 10px;
      }

      .promo-popup-discount {
        font-size: 20px;
      }
    }
  `;
  document.head.appendChild(style);

  // Create overlay
  const overlay = document.createElement('div');
  overlay.className = 'promo-popup-overlay';
  overlay.innerHTML = `
    <div class="promo-popup-card">
      <button class="promo-popup-close" onclick="closePromoPopup()" aria-label="Close">&times;</button>
      <div class="promo-popup-header">
        <div class="promo-popup-icon">🎉</div>
        <h2 class="promo-popup-title">Exclusive <span>Offers</span></h2>
        <p class="promo-popup-subtitle">Grab these deals before they expire</p>
      </div>
      <div class="promo-popup-body" id="promoPopupBody">
        <div class="promo-popup-loading">Loading offers...</div>
      </div>
      <div class="promo-popup-footer">
        <button class="promo-popup-dismiss" onclick="closePromoPopup()">Maybe Later</button>
      </div>
    </div>
  `;
  document.body.appendChild(overlay);

  // Fetch active promos
  const API_BASE = 'http://localhost:8080/api';

  fetch(API_BASE + '/promo-codes/active')
    .then(function (res) { return res.json(); })
    .then(function (data) {
      var promos = (data && data.data) || [];
      if (!promos.length) {
        overlay.remove();
        return;
      }

      var body = document.getElementById('promoPopupBody');
      if (!body) return;

      body.innerHTML = promos.map(function (p) {
        var discountText = p.discountType === 'PERCENTAGE'
          ? p.discountValue + '% OFF'
          : '৳' + parseFloat(p.discountValue).toLocaleString() + ' OFF';

        return (
          '<div class="promo-popup-item">' +
            '<div class="promo-popup-item-left">' +
              '<div class="promo-popup-code">' + p.code + '</div>' +
              '<div class="promo-popup-desc">' + (p.description || 'Special discount') + '</div>' +
            '</div>' +
            '<div class="promo-popup-item-right">' +
              '<div class="promo-popup-discount">' + discountText + '</div>' +
              '<button class="promo-popup-copy" onclick="copyPromoCode(\'' + p.code + '\')">Copy</button>' +
            '</div>' +
          '</div>'
        );
      }).join('');

      // Auto-show after 2 seconds
      setTimeout(function () {
        overlay.classList.add('active');
      }, 2000);
    })
    .catch(function () {
      overlay.remove();
    });

  // Close popup
  window.closePromoPopup = function () {
    overlay.classList.remove('active');
    localStorage.setItem('promoPopupDate', today);
    setTimeout(function () { overlay.remove(); }, 400);
  };

  // Copy code
  window.copyPromoCode = function (code) {
    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(code).then(function () { showCopyToast(code); });
    } else {
      var ta = document.createElement('textarea');
      ta.value = code;
      ta.style.position = 'fixed';
      ta.style.opacity = '0';
      document.body.appendChild(ta);
      ta.select();
      document.execCommand('copy');
      document.body.removeChild(ta);
      showCopyToast(code);
    }
  };

  function showCopyToast(code) {
    var t = document.createElement('div');
    t.className = 'promo-popup-toast';
    t.textContent = '✓ Code copied: ' + code;
    document.body.appendChild(t);
    setTimeout(function () { t.remove(); }, 2200);
  }

  // ESC key closes popup
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape' && overlay.classList.contains('active')) {
      window.closePromoPopup();
    }
  });

  // Click outside closes
  overlay.addEventListener('click', function (e) {
    if (e.target === overlay) {
      window.closePromoPopup();
    }
  });
})();
