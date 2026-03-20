// Dashboard JavaScript
class DashboardManager {
    constructor() {
        this.currentPage = 'overview';
        this.charts = {};
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadUserData();
        this.loadOverviewData();
        this.initializeCharts();
    }

    setupEventListeners() {
        // Navigation
        document.querySelectorAll('[data-page]').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = e.target.closest('[data-page]').dataset.page;
                this.navigateToPage(page);
            });
        });

        // Logout
        document.getElementById('logoutBtn').addEventListener('click', (e) => {
            e.preventDefault();
            this.handleLogout();
        });

        // Period buttons
        document.querySelectorAll('.btn-toolbar .btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                this.updatePeriod(e.target.textContent.trim());
            });
        });
    }

    async loadUserData() {
        const user = AuthManager.getCurrentUser();
        if (user) {
            document.getElementById('userName').textContent = 
                `${user.firstName} ${user.lastName}`;
        }
    }

    navigateToPage(page) {
        // Update active states
        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.remove('active');
        });
        document.querySelectorAll(`[data-page="${page}"]`).forEach(link => {
            link.classList.add('active');
        });

        // Show/hide pages
        document.querySelectorAll('.page-content').forEach(content => {
            content.classList.remove('active');
        });
        document.getElementById(`${page}Page`).classList.add('active');

        this.currentPage = page;
        this.loadPageData(page);
    }

    async loadPageData(page) {
        this.showLoading(true);

        try {
            switch (page) {
                case 'overview':
                    await this.loadOverviewData();
                    break;
                case 'products':
                    await this.loadProductsData();
                    break;
                case 'orders':
                    await this.loadOrdersData();
                    break;
                case 'artisans':
                    await this.loadArtisansData();
                    break;
                case 'customers':
                    await this.loadCustomersData();
                    break;
                case 'profile':
                    await this.loadProfileData();
                    break;
                case 'settings':
                    await this.loadSettingsData();
                    break;
                case 'reports':
                    await this.loadReportsData();
                    break;
                case 'revenue':
                    await this.loadRevenueData();
                    break;
            }
        } catch (error) {
            console.error(`Error loading ${page} data:`, error);
            this.showToast('Error loading page data', 'error');
        } finally {
            this.showLoading(false);
        }
    }

    async loadOverviewData() {
        try {
            // Load stats
            const [products, orders, artisans, revenue] = await Promise.all([
                this.apiCall('/secure/products/count'),
                this.apiCall('/secure/orders/count'),
                this.apiCall('/secure/artisans/count'),
                this.apiCall('/secure/orders/revenue')
            ]);

            document.getElementById('totalProducts').textContent = products || 0;
            document.getElementById('totalOrders').textContent = orders || 0;
            document.getElementById('totalArtisans').textContent = artisans || 0;
            document.getElementById('totalRevenue').textContent = `$${revenue || 0}`;

            // Load recent orders
            await this.loadRecentOrders();

            // Update charts
            this.updateCharts();
        } catch (error) {
            console.error('Error loading overview data:', error);
        }
    }

    async loadRecentOrders() {
        try {
            const orders = await this.apiCall('/secure/orders?size=10');
            const tbody = document.querySelector('#recentOrdersTable tbody');
            
            tbody.innerHTML = '';
            
            if (orders && orders.length > 0) {
                orders.forEach(order => {
                    const row = this.createOrderRow(order);
                    tbody.appendChild(row);
                });
            } else {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center">No orders found</td></tr>';
            }
        } catch (error) {
            console.error('Error loading recent orders:', error);
        }
    }

    createOrderRow(order) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>#${order.orderId}</td>
            <td>${order.customer ? order.customer.firstName + ' ' + order.customer.lastName : 'N/A'}</td>
            <td>$${order.totalAmount || 0}</td>
            <td><span class="badge badge-${order.orderStatus ? order.orderStatus.toLowerCase() : 'pending'}">${order.orderStatus || 'PENDING'}</span></td>
            <td>${new Date(order.orderDate).toLocaleDateString()}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-sm btn-outline-primary" onclick="dashboard.viewOrder(${order.orderId})">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-secondary" onclick="dashboard.editOrder(${order.orderId})">
                        <i class="fas fa-edit"></i>
                    </button>
                </div>
            </td>
        `;
        return row;
    }

    async loadProductsData() {
        // Implementation for products page
        console.log('Loading products data...');
    }

    async loadOrdersData() {
        // Implementation for orders page
        console.log('Loading orders data...');
    }

    async loadArtisansData() {
        // Implementation for artisans page
        console.log('Loading artisans data...');
    }

    async loadCustomersData() {
        // Implementation for customers page
        console.log('Loading customers data...');
    }

    async loadProfileData() {
        // Implementation for profile page
        console.log('Loading profile data...');
    }

    async loadSettingsData() {
        // Implementation for settings page
        console.log('Loading settings data...');
    }

    async loadReportsData() {
        // Implementation for reports page
        console.log('Loading reports data...');
    }

    async loadRevenueData() {
        // Implementation for revenue page
        console.log('Loading revenue data...');
    }

    initializeCharts() {
        // Revenue Chart
        const revenueCtx = document.getElementById('revenueChart');
        if (revenueCtx) {
            this.charts.revenue = new Chart(revenueCtx, {
                type: 'line',
                data: {
                    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                    datasets: [{
                        label: 'Revenue',
                        data: [12000, 19000, 15000, 25000, 22000, 30000],
                        borderColor: '#667eea',
                        backgroundColor: 'rgba(102, 126, 234, 0.1)',
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return '$' + value.toLocaleString();
                                }
                            }
                        }
                    }
                }
            });
        }

        // Order Status Chart
        const orderStatusCtx = document.getElementById('orderStatusChart');
        if (orderStatusCtx) {
            this.charts.orderStatus = new Chart(orderStatusCtx, {
                type: 'doughnut',
                data: {
                    labels: ['Pending', 'Paid', 'Processing', 'Shipped', 'Delivered'],
                    datasets: [{
                        data: [12, 19, 8, 15, 25],
                        backgroundColor: [
                            '#ffc107',
                            '#17a2b8',
                            '#667eea',
                            '#6f42c1',
                            '#28a745'
                        ],
                        borderWidth: 0
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
        }
    }

    updateCharts() {
        // Update charts with real data
        // This would be called after fetching data from API
    }

    updatePeriod(period) {
        // Update data based on selected period
        console.log(`Updating data for period: ${period}`);
        this.loadOverviewData();
    }

    async apiCall(endpoint) {
        try {
            const response = await window.apiClient.get(endpoint);
            if (response.ok) {
                return await response.json();
            } else {
                throw new Error(`API call failed: ${response.status}`);
            }
        } catch (error) {
            console.error('API call error:', error);
            throw error;
        }
    }

    showLoading(show) {
        const overlay = document.getElementById('loadingOverlay');
        if (show) {
            overlay.classList.remove('d-none');
        } else {
            overlay.classList.add('d-none');
        }
    }

    showToast(message, type = 'info') {
        const toastElement = document.getElementById('liveToast');
        const toastMessage = document.getElementById('toastMessage');
        
        toastMessage.textContent = message;
        
        const toast = new bootstrap.Toast(toastElement);
        toast.show();
    }

    handleLogout() {
        if (confirm('Are you sure you want to logout?')) {
            AuthManager.logout();
        }
    }

    // Order actions
    viewOrder(orderId) {
        console.log(`Viewing order ${orderId}`);
        // Implement order view modal
    }

    editOrder(orderId) {
        console.log(`Editing order ${orderId}`);
        // Implement order edit modal
    }

    // Product actions
    addProduct() {
        console.log('Adding new product');
        // Implement add product modal
    }

    editProduct(productId) {
        console.log(`Editing product ${productId}`);
        // Implement edit product modal
    }

    deleteProduct(productId) {
        if (confirm('Are you sure you want to delete this product?')) {
            console.log(`Deleting product ${productId}`);
            // Implement product deletion
        }
    }

    // User actions
    editUser(userId) {
        console.log(`Editing user ${userId}`);
        // Implement user edit modal
    }

    deleteUser(userId) {
        if (confirm('Are you sure you want to delete this user?')) {
            console.log(`Deleting user ${userId}`);
            // Implement user deletion
        }
    }

    // Utility methods
    formatCurrency(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    }

    formatDate(date) {
        return new Date(date).toLocaleDateString();
    }

    getStatusBadgeClass(status) {
        const statusMap = {
            'PENDING': 'badge-pending',
            'PAID': 'badge-paid',
            'PROCESSING': 'badge-processing',
            'SHIPPED': 'badge-shipped',
            'DELIVERED': 'badge-delivered',
            'CANCELLED': 'badge-cancelled'
        };
        return statusMap[status] || 'badge-secondary';
    }
}

// Initialize Dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    // Check if user is authenticated
    if (!AuthManager.isLoggedIn()) {
        window.location.href = '/auth.html';
        return;
    }

    // Initialize dashboard
    window.dashboard = new DashboardManager();
});

// Handle browser back/forward
window.addEventListener('popstate', (e) => {
    if (e.state && e.state.page) {
        window.dashboard.navigateToPage(e.state.page);
    }
});

// Auto-refresh data every 5 minutes
setInterval(() => {
    if (window.dashboard) {
        window.dashboard.loadPageData(window.dashboard.currentPage);
    }
}, 5 * 60 * 1000);

// Handle online/offline status
window.addEventListener('online', () => {
    if (window.dashboard) {
        window.dashboard.showToast('Connection restored', 'success');
        window.dashboard.loadPageData(window.dashboard.currentPage);
    }
});

window.addEventListener('offline', () => {
    if (window.dashboard) {
        window.dashboard.showToast('Connection lost', 'warning');
    }
});

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
    // Ctrl/Cmd + K for search
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        // Focus on search input
        const searchInput = document.querySelector('.search-bar input');
        if (searchInput) {
            searchInput.focus();
        }
    }
    
    // Escape to close modals
    if (e.key === 'Escape') {
        const openModal = document.querySelector('.modal.show');
        if (openModal) {
            bootstrap.Modal.getInstance(openModal).hide();
        }
    }
});

// Print functionality
function printPage() {
    window.print();
}

// Export functionality
function exportData(format) {
    console.log(`Exporting data in ${format} format`);
    // Implement export functionality
}

// Search functionality
function searchItems(query) {
    console.log(`Searching for: ${query}`);
    // Implement search functionality
}

// Filter functionality
function filterData(filters) {
    console.log('Applying filters:', filters);
    // Implement filter functionality
}

// Sort functionality
function sortData(column, direction) {
    console.log(`Sorting by ${column} ${direction}`);
    // Implement sort functionality
}

// Pagination
function changePage(page) {
    console.log(`Changing to page ${page}`);
    // Implement pagination
}

// Bulk actions
function selectAllItems(selectAll) {
    const checkboxes = document.querySelectorAll('.item-checkbox');
    checkboxes.forEach(checkbox => {
        checkbox.checked = selectAll;
    });
}

function performBulkAction(action) {
    const selectedItems = document.querySelectorAll('.item-checkbox:checked');
    console.log(`Performing ${action} on ${selectedItems.length} items`);
    // Implement bulk action functionality
}
