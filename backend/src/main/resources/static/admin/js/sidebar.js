/* ===============================================
   Sidebar - Interaction Logic
   =============================================== */

/**
 * Toggle sidebar on mobile
 */
function toggleSidebar() {
    const sidebar = document.getElementById('adminSidebar');
    const backdrop = document.getElementById('sidebarBackdrop');

    if (sidebar && backdrop) {
        sidebar.classList.toggle('open');
        backdrop.classList.toggle('show');

        // Prevent body scroll when sidebar is open on mobile
        if (sidebar.classList.contains('open')) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = '';
        }
    }
}

/**
 * Close sidebar
 */
function closeSidebar() {
    const sidebar = document.getElementById('adminSidebar');
    const backdrop = document.getElementById('sidebarBackdrop');

    if (sidebar && backdrop) {
        sidebar.classList.remove('open');
        backdrop.classList.remove('show');
        document.body.style.overflow = '';
    }
}

/**
 * Handle backdrop click
 */
function handleBackdropClick(event) {
    if (event.target.id === 'sidebarBackdrop') {
        closeSidebar();
    }
}

/**
 * Initialize sidebar interactions
 */
function initSidebarInteractions() {
    // Wait for sidebar to be injected
    setTimeout(() => {
        // Hamburger button click
        const hamburgerBtn = document.getElementById('hamburgerBtn');
        if (hamburgerBtn) {
            hamburgerBtn.addEventListener('click', toggleSidebar);
        }

        // Close button click
        const closeBtn = document.getElementById('sidebarCloseBtn');
        if (closeBtn) {
            closeBtn.addEventListener('click', closeSidebar);
        }

        // Backdrop click
        const backdrop = document.getElementById('sidebarBackdrop');
        if (backdrop) {
            backdrop.addEventListener('click', handleBackdropClick);
        }

        // Close sidebar on navigation (mobile only)
        const navItems = document.querySelectorAll('.nav-item');
        navItems.forEach(item => {
            item.addEventListener('click', () => {
                if (window.innerWidth < 992) {
                    closeSidebar();
                }
            });
        });
    }, 100);
}

// Initialize on DOM content loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initSidebarInteractions);
} else {
    initSidebarInteractions();
}

// Close sidebar on window resize to desktop
window.addEventListener('resize', () => {
    if (window.innerWidth >= 992) {
        closeSidebar();
    }
});
