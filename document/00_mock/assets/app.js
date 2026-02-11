(function(){
  function q(sel, root=document){ return root.querySelector(sel); }
  function qa(sel, root=document){ return Array.from(root.querySelectorAll(sel)); }

  // Sidebar toggle (mobile)
  const sidebar = q('#sidebar');
  const openSidebarBtn = q('#openSidebarBtn');
  const closeSidebarBtn = q('#closeSidebarBtn');
  const overlayShade = q('#sidebarShade');

  function openSidebar(){
    if(!sidebar) return;
    sidebar.classList.add('open');
    if(overlayShade) overlayShade.classList.add('show');
  }
  function closeSidebar(){
    if(!sidebar) return;
    sidebar.classList.remove('open');
    if(overlayShade) overlayShade.classList.remove('show');
  }
  openSidebarBtn && openSidebarBtn.addEventListener('click', openSidebar);
  closeSidebarBtn && closeSidebarBtn.addEventListener('click', closeSidebar);
  overlayShade && overlayShade.addEventListener('click', closeSidebar);

  // Generic modal: data-open-modal="#id", data-close-modal
  qa('[data-open-modal]').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      const target = btn.getAttribute('data-open-modal');
      const ov = q(target);
      if(!ov) return;
      ov.classList.add('show');
      ov.setAttribute('aria-hidden','false');

      // Auto fill datetime-local if present
      const dt = q('input[type="datetime-local"]', ov);
      if(dt && !dt.value){
        const now = new Date();
        const pad = (n)=> String(n).padStart(2,'0');
        dt.value = `${now.getFullYear()}-${pad(now.getMonth()+1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
      }
    });
  });

  qa('[data-close-modal]').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      const ov = btn.closest('.overlay');
      if(!ov) return;
      ov.classList.remove('show');
      ov.setAttribute('aria-hidden','true');
    });
  });

  qa('.overlay').forEach(ov=>{
    ov.addEventListener('click', (e)=>{
      if(e.target === ov){
        ov.classList.remove('show');
        ov.setAttribute('aria-hidden','true');
      }
    });
  });

  window.addEventListener('keydown', (e)=>{
    if(e.key === 'Escape'){
      qa('.overlay.show').forEach(ov=>{
        ov.classList.remove('show');
        ov.setAttribute('aria-hidden','true');
      });
      closeSidebar();
    }
  });

  // Tabs: [data-tabs] container with buttons [data-tab] and panels [data-panel]
  qa('[data-tabs]').forEach(container=>{
    const tabs = qa('[data-tab]', container);
    const panels = qa('[data-panel]', container);
    const setActive = (name)=>{
      tabs.forEach(t=> t.classList.toggle('active', t.getAttribute('data-tab')===name));
      panels.forEach(p=> p.style.display = (p.getAttribute('data-panel')===name ? 'block' : 'none'));
    };
    if(tabs.length){
      const def = container.getAttribute('data-default-tab') || tabs[0].getAttribute('data-tab');
      setActive(def);
      tabs.forEach(t=>{
        t.addEventListener('click', ()=> setActive(t.getAttribute('data-tab')));
      });
    }
  });

  // Transactions table row -> detail panel + optional navigate button
  const txTable = q('#txTable');
  const detailHint = q('#detailHint');
  const detailKv = q('#detailKv');
  if(txTable && detailKv){
    const clearSelected = ()=> qa('tbody tr', txTable).forEach(tr=> tr.classList.remove('selected'));
    txTable.addEventListener('click', (e)=>{
      const tr = e.target.closest('tr');
      if(!tr) return;
      if(e.target.matches('input[type="checkbox"]')) return; // keep click behavior simple
      clearSelected();
      tr.classList.add('selected');

      const tds = tr.querySelectorAll('td');
      const id = tr.getAttribute('data-id') || '-';
      const date = (tds[1]?.textContent||'').trim();
      const type = (tds[2]?.textContent||'').trim();
      const merchant = (tds[3]?.textContent||'').trim();
      const category = (tds[4]?.textContent||'').trim();
      const method = (tds[5]?.textContent||'').trim();
      const amount = (tds[6]?.textContent||'').trim();
      const memo = (tds[7]?.textContent||'').trim();
      const status = (tds[8]?.textContent||'').trim();

      if(detailHint) detailHint.textContent = `${merchant} · ${date}`;

      const kvs = [
        ['거래ID', id],
        ['일시', date],
        ['유형', type],
        ['내용', merchant],
        ['카테고리', category.replace(/\s+/g,' ')],
        ['결제수단', method],
        ['금액', amount],
        ['메모', memo || '-'],
        ['상태', status || '-'],
      ];
      detailKv.innerHTML = kvs.map(([k,v])=>(
        `<div class="kv"><div class="k">${k}</div><div class="v" title="${v}">${v}</div></div>`
      )).join('');

      const openBtn = q('#openDetailBtn');
      if(openBtn){
        openBtn.onclick = ()=>{ window.location.href = `transaction_detail.html?id=${encodeURIComponent(id)}`; };
      }
    });
  }

  // Transaction detail: read query param and set id field
  const idHolder = q('[data-tx-id]');
  if(idHolder){
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id') || 'T-EXAMPLE-000';
    idHolder.textContent = id;
    const input = q('#txIdInput');
    if(input) input.value = id;
  }

  // Quick toast (mock)
  qa('[data-toast]').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      const msg = btn.getAttribute('data-toast') || '완료(목업)';
      alert(msg);
    });
  });
})();
