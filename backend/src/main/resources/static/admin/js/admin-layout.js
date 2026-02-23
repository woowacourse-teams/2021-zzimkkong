/* ===============================================
   Admin Layout - Sidebar Generation & Auth
   =============================================== */

// Menu configuration
const MENU_ITEMS = [
    { id: 'members', label: '회원 관리', icon: 'bi-people-fill', url: '/admin/members' },
    { id: 'maps', label: '맵 관리', icon: 'bi-map-fill', url: '/admin/maps' },
    { id: 'spaces', label: '공간 관리', icon: 'bi-grid-3x3-gap-fill', url: '/admin/spaces' },
    { id: 'reservations', label: '예약 관리', icon: 'bi-calendar-check-fill', url: '/admin/reservations' }
];

/**
 * Generate sidebar HTML
 */
function generateSidebar() {
    const navItems = MENU_ITEMS.map(item => `
        <a href="${item.url}" class="nav-item" data-page="${item.id}">
            <i class="bi ${item.icon}"></i>
            <span class="nav-item-text">${item.label}</span>
        </a>
    `).join('');

    return `
        <aside class="admin-sidebar" id="adminSidebar">
            <button class="sidebar-close-btn" id="sidebarCloseBtn">
                <i class="bi bi-x"></i>
            </button>

            <div class="sidebar-header">
                <a href="/admin/members" class="sidebar-logo">
                    <i class="bi bi-calendar-check-fill"></i>
                    <div>
                        <div class="sidebar-logo-text">찜꽁</div>
                        <div class="sidebar-logo-subtitle">관리자</div>
                    </div>
                </a>
            </div>

            <nav class="sidebar-nav">
                ${navItems}
            </nav>

            <div class="sidebar-footer">
                <button class="logout-btn" onclick="handleLogout()">
                    <i class="bi bi-box-arrow-right"></i>
                    <span>로그아웃</span>
                </button>
            </div>
        </aside>
        <div class="sidebar-backdrop" id="sidebarBackdrop"></div>
    `;
}

/**
 * Set active menu item based on current page
 */
function setActiveMenuItem() {
    const currentPath = window.location.pathname;
    const navItems = document.querySelectorAll('.nav-item');

    navItems.forEach(item => {
        const itemPath = item.getAttribute('href');
        if (currentPath === itemPath || currentPath.includes(itemPath)) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });
}

/**
 * Check authentication
 */
function checkAuth() {
    const token = window.localStorage.getItem('accessToken');

    // If no token and not on login page, redirect to login
    if (!token && !window.location.pathname.includes('/admin/login')) {
        window.location.href = '/admin/login';
        return false;
    }

    // If has token and on login page, redirect to members page
    if (token && window.location.pathname.includes('/admin/login')) {
        window.location.href = '/admin/members';
        return false;
    }

    return true;
}

/**
 * Handle logout
 */
function handleLogout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        window.localStorage.removeItem('accessToken');
        window.location.href = '/admin/login';
    }
}

/**
 * Handle 401 Unauthorized error
 * Removes expired token and redirects to login page
 */
function handleUnauthorized() {
    // Remove expired token to prevent infinite redirect loop
    window.localStorage.removeItem('accessToken');

    // Show user-friendly message
    alert('로그인 세션이 만료되었습니다. 다시 로그인해주세요.');

    // Redirect to login page
    window.location.href = '/admin/login';
}

/**
 * Initialize admin layout
 */
function initAdminLayout() {
    // Check authentication first
    if (!checkAuth()) {
        return;
    }

    // Don't inject sidebar on login page
    if (window.location.pathname.includes('/admin/login')) {
        return;
    }

    // Inject sidebar HTML
    const sidebarHTML = generateSidebar();
    document.body.insertAdjacentHTML('afterbegin', sidebarHTML);

    // Set active menu item
    setActiveMenuItem();
}

// Initialize on DOM content loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAdminLayout);
} else {
    initAdminLayout();
}
