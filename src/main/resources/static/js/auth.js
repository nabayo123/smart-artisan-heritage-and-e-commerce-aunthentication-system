// Authentication JavaScript
class AuthManager {
    constructor() {
        this.apiBaseUrl = '/api/auth';
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupPasswordToggle();
        this.setupPasswordStrength();
        this.setupUserTypeToggle();
        this.setupFormValidation();
    }

    setupEventListeners() {
        // Login form submission
        document.getElementById('loginForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });

        // Register form submission
        document.getElementById('registerForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleRegister();
        });

        // User type radio buttons
        document.querySelectorAll('input[name="userType"]').forEach(radio => {
            radio.addEventListener('change', (e) => {
                this.toggleArtisanFields(e.target.value === 'ARTISAN');
            });
        });
    }

    setupPasswordToggle() {
        // Login password toggle
        document.getElementById('toggleLoginPassword').addEventListener('click', () => {
            this.togglePasswordVisibility('loginPassword', 'toggleLoginPassword');
        });

        // Register password toggle
        document.getElementById('toggleRegisterPassword').addEventListener('click', () => {
            this.togglePasswordVisibility('registerPassword', 'toggleRegisterPassword');
        });

        // Confirm password toggle
        document.getElementById('toggleConfirmPassword').addEventListener('click', () => {
            this.togglePasswordVisibility('confirmPassword', 'toggleConfirmPassword');
        });
    }

    togglePasswordVisibility(inputId, buttonId) {
        const input = document.getElementById(inputId);
        const button = document.getElementById(buttonId);
        const icon = button.querySelector('i');

        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    }

    setupPasswordStrength() {
        const passwordInput = document.getElementById('registerPassword');
        passwordInput.addEventListener('input', (e) => {
            this.checkPasswordStrength(e.target.value);
        });
    }

    checkPasswordStrength(password) {
        const strengthBar = document.getElementById('passwordStrength');
        const strengthText = document.getElementById('passwordStrengthText');
        
        let strength = 0;
        
        // Length check
        if (password.length >= 8) strength++;
        if (password.length >= 12) strength++;
        
        // Complexity checks
        if (/[a-z]/.test(password)) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^a-zA-Z0-9]/.test(password)) strength++;

        // Update UI
        strengthBar.className = 'progress-bar';
        
        if (password.length === 0) {
            strengthBar.style.width = '0%';
            strengthText.textContent = 'Password strength';
            strengthText.className = 'text-muted';
        } else if (strength <= 2) {
            strengthBar.classList.add('weak');
            strengthText.textContent = 'Weak password';
            strengthText.className = 'text-danger';
        } else if (strength <= 4) {
            strengthBar.classList.add('medium');
            strengthText.textContent = 'Medium strength';
            strengthText.className = 'text-warning';
        } else {
            strengthBar.classList.add('strong');
            strengthText.textContent = 'Strong password';
            strengthText.className = 'text-success';
        }
    }

    setupUserTypeToggle() {
        // Initial state
        const artisanType = document.getElementById('artisanType');
        if (artisanType && artisanType.checked) {
            this.toggleArtisanFields(true);
        }
    }

    toggleArtisanFields(show) {
        const artisanFields = document.getElementById('artisanFields');
        const specialization = document.getElementById('specialization');
        const province = document.getElementById('province');

        if (show) {
            artisanFields.classList.remove('d-none');
            artisanFields.classList.add('show');
            specialization.setAttribute('required', '');
            province.setAttribute('required', '');
        } else {
            artisanFields.classList.add('d-none');
            artisanFields.classList.remove('show');
            specialization.removeAttribute('required');
            province.removeAttribute('required');
        }
    }

    setupFormValidation() {
        // Bootstrap form validation
        const forms = document.querySelectorAll('.needs-validation');
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });

        // Custom validation
        this.setupCustomValidation();
    }

    setupCustomValidation() {
        // Password confirmation validation
        const confirmPassword = document.getElementById('confirmPassword');
        const registerPassword = document.getElementById('registerPassword');

        confirmPassword.addEventListener('input', () => {
            if (confirmPassword.value !== registerPassword.value) {
                confirmPassword.setCustomValidity('Passwords do not match');
            } else {
                confirmPassword.setCustomValidity('');
            }
        });

        registerPassword.addEventListener('input', () => {
            if (confirmPassword.value && confirmPassword.value !== registerPassword.value) {
                confirmPassword.setCustomValidity('Passwords do not match');
            } else {
                confirmPassword.setCustomValidity('');
            }
        });
    }

    async handleLogin() {
        const form = document.getElementById('loginForm');
        const formData = new FormData(form);
        const loginData = {
            email: formData.get('email'),
            password: formData.get('password')
        };

        try {
            this.showLoading(true);
            this.hideMessage();

            const response = await fetch(`${this.apiBaseUrl}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData)
            });

            const result = await response.json();

            if (response.ok) {
                // Store JWT token
                localStorage.setItem('jwtToken', result.token);
                localStorage.setItem('user', JSON.stringify(result.user));
                
                this.showMessage('Login successful! Redirecting to dashboard...', 'success');
                
                // Redirect to dashboard after delay
                setTimeout(() => {
                    window.location.href = '/dashboard.html';
                }, 1500);
            } else {
                this.showMessage(result.message || 'Login failed. Please check your credentials.', 'danger');
            }
        } catch (error) {
            console.error('Login error:', error);
            this.showMessage('Network error. Please try again.', 'danger');
        } finally {
            this.showLoading(false);
        }
    }

    async handleRegister() {
        const form = document.getElementById('registerForm');
        
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        const formData = new FormData(form);
        const registerData = {
            userType: formData.get('userType'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            email: formData.get('email'),
            password: formData.get('password')
        };

        // Add artisan-specific fields if applicable
        if (registerData.userType === 'ARTISAN') {
            registerData.specialization = formData.get('specialization');
            registerData.province = formData.get('province');
            registerData.cooperative = formData.get('cooperative');
        }

        try {
            this.showLoading(true);
            this.hideMessage();

            const response = await fetch(`${this.apiBaseUrl}/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(registerData)
            });

            const result = await response.json();

            if (response.ok) {
                this.showMessage('Registration successful! You can now login.', 'success');
                
                // Switch to login tab after successful registration
                setTimeout(() => {
                    const loginTab = new bootstrap.Tab(document.getElementById('login-tab'));
                    loginTab.show();
                    form.reset();
                    form.classList.remove('was-validated');
                }, 2000);
            } else {
                this.showMessage(result.message || 'Registration failed. Please try again.', 'danger');
            }
        } catch (error) {
            console.error('Registration error:', error);
            this.showMessage('Network error. Please try again.', 'danger');
        } finally {
            this.showLoading(false);
        }
    }

    showLoading(show) {
        const spinner = document.getElementById('loadingSpinner');
        const forms = document.querySelectorAll('#loginForm, #registerForm');
        
        if (show) {
            spinner.classList.remove('d-none');
            forms.forEach(form => form.style.display = 'none');
        } else {
            spinner.classList.add('d-none');
            forms.forEach(form => form.style.display = 'block');
        }
    }

    showMessage(message, type) {
        const messageDiv = document.getElementById('authMessage');
        messageDiv.textContent = message;
        messageDiv.className = `alert alert-${type}`;
        messageDiv.classList.remove('d-none');
        
        // Auto-hide success messages after 5 seconds
        if (type === 'success') {
            setTimeout(() => {
                this.hideMessage();
            }, 5000);
        }
    }

    hideMessage() {
        const messageDiv = document.getElementById('authMessage');
        messageDiv.classList.add('d-none');
    }

    // Utility method to check if user is logged in
    static isLoggedIn() {
        return localStorage.getItem('jwtToken') !== null;
    }

    // Utility method to get current user
    static getCurrentUser() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    }

    // Utility method to logout
    static logout() {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('user');
        window.location.href = '/auth.html';
    }

    // Utility method to get JWT token
    static getJwtToken() {
        return localStorage.getItem('jwtToken');
    }
}

// Initialize Auth Manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new AuthManager();
});

// Check if user is already logged in
if (AuthManager.isLoggedIn()) {
    // Redirect to dashboard if already logged in
    window.location.href = '/dashboard.html';
}

// Utility functions for API calls
class ApiClient {
    constructor() {
        this.baseUrl = '/api';
    }

    async request(endpoint, options = {}) {
        const token = AuthManager.getJwtToken();
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        const response = await fetch(`${this.baseUrl}${endpoint}`, config);
        
        if (response.status === 401) {
            // Token expired or invalid
            AuthManager.logout();
            throw new Error('Session expired. Please login again.');
        }

        return response;
    }

    async get(endpoint) {
        return this.request(endpoint);
    }

    async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async delete(endpoint) {
        return this.request(endpoint, {
            method: 'DELETE'
        });
    }
}

// Global API client instance
window.apiClient = new ApiClient();
