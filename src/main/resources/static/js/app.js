// Base API URL
const API_BASE = '/api/auth';

// Utility for showing alerts
function showAlert(elementId, message, type = 'error') {
    const alertEl = document.getElementById(elementId);
    if (!alertEl) return;
    
    alertEl.textContent = message;
    alertEl.className = `alert ${type}`;
    alertEl.classList.remove('hidden');
}

function hideAlert(elementId) {
    const alertEl = document.getElementById(elementId);
    if (alertEl) alertEl.classList.add('hidden');
}

// Setup Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    
    // --- Register Page Logic ---
    const userTypeSelect = document.getElementById('userType');
    const artisanFields = document.getElementById('artisanFields');
    
    // Check URL params for pre-selecting userType
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('type') === 'ARTISAN' && userTypeSelect) {
        userTypeSelect.value = 'ARTISAN';
        if (artisanFields) artisanFields.classList.remove('hidden');
    }
    
    if (userTypeSelect && artisanFields) {
        userTypeSelect.addEventListener('change', (e) => {
            if (e.target.value === 'ARTISAN') {
                artisanFields.classList.remove('hidden');
                document.getElementById('businessName').required = true;
                document.getElementById('description').required = true;
            } else {
                artisanFields.classList.add('hidden');
                document.getElementById('businessName').required = false;
                document.getElementById('description').required = false;
            }
        });
    }

    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            hideAlert('registerAlert');
            
            const btn = document.getElementById('registerSubmitBtn');
            const btnText = btn.querySelector('.btn-text');
            const loader = btn.querySelector('.loader');
            
            btn.disabled = true;
            btnText.classList.add('hidden');
            loader.classList.remove('hidden');

            const payload = {
                firstName: document.getElementById('firstName').value.trim(),
                lastName: document.getElementById('lastName').value.trim(),
                email: document.getElementById('email').value.trim(),
                phoneNumber: document.getElementById('phoneNumber').value.trim(),
                password: document.getElementById('password').value,
                userType: document.getElementById('userType').value,
                businessName: document.getElementById('businessName') ? document.getElementById('businessName').value.trim() : null,
                description: document.getElementById('description') ? document.getElementById('description').value.trim() : null
            };

            try {
                const response = await fetch(`${API_BASE}/register`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                // Read JSON response (or text if no json provided)
                let data = null;
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    data = await response.json();
                } else {
                    data = { message: await response.text() };
                }

                if (response.ok) {
                    showAlert('registerAlert', 'Registration successful! Redirecting...', 'success');
                    if (data && data.jwt) {
                        localStorage.setItem('token', data.jwt);
                        localStorage.setItem('user', JSON.stringify(data));
                    }
                    setTimeout(() => window.location.href = '/index.html', 1500);
                } else {
                    const errMsg = (data && data.message) ? data.message : 'Registration failed. Please check your inputs.';
                    showAlert('registerAlert', errMsg, 'error');
                }
            } catch (error) {
                showAlert('registerAlert', 'Network error. Make sure the backend is running.', 'error');
                console.error(error);
            } finally {
                btn.disabled = false;
                btnText.classList.remove('hidden');
                loader.classList.add('hidden');
            }
        });
    }

    // --- Login Page Logic ---
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            hideAlert('loginAlert');
            
            const btn = document.getElementById('loginSubmitBtn');
            const btnText = btn.querySelector('.btn-text');
            const loader = btn.querySelector('.loader');
            
            btn.disabled = true;
            btnText.classList.add('hidden');
            loader.classList.remove('hidden');

            const payload = {
                email: document.getElementById('email').value.trim(),
                password: document.getElementById('password').value
            };

            try {
                const response = await fetch(`${API_BASE}/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                let data = null;
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    data = await response.json();
                }

                if (response.ok) {
                    showAlert('loginAlert', 'Login successful! Redirecting...', 'success');
                    if (data && data.jwt) {
                        localStorage.setItem('token', data.jwt);
                        localStorage.setItem('user', JSON.stringify(data));
                    }
                    setTimeout(() => window.location.href = '/index.html', 1000);
                } else {
                    showAlert('loginAlert', 'Invalid email or password.', 'error');
                }
            } catch (error) {
                showAlert('loginAlert', 'Network error. Make sure the backend is running.', 'error');
                console.error(error);
            } finally {
                btn.disabled = false;
                btnText.classList.remove('hidden');
                loader.classList.add('hidden');
            }
        });
    }
    
    // --- Index/Global State Logic ---
    const token = localStorage.getItem('token');
    const userString = localStorage.getItem('user');
    const navLinks = document.querySelector('.nav-links');
    
    // If logged in and on the homepage, show user name
    if (token && userString) {
        try {
            const user = JSON.parse(userString);
            if(navLinks && (window.location.pathname.endsWith('index.html') || window.location.pathname === '/')) {
                navLinks.innerHTML = `
                    <span style="margin-right: 15px; font-weight: 500; font-size: 0.95rem;">Hello, <span class="highlight">${user.firstName || userString}</span></span>
                    <button onclick="logout()" class="nav-btn btn-ghost">Logout</button>
                    ${user.userType === 'ADMIN' ? '<a href="/admin/dashboard.html" class="nav-btn btn-primary">Dashboard</a>' : ''}
                `;
            }
        } catch(e) { console.error("Could not parse user data"); }
    }
});

// Global Logout
window.logout = function() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.reload();
}
