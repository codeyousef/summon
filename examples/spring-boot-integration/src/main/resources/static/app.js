// DISABLED: Custom JavaScript replaced by Summon framework hydration
// JWT Todo App Client-Side JavaScript
/*
class TodoApp {
    constructor() {
        this.apiBase = '/api';
        this.token = localStorage.getItem('jwt_token');
        this.user = JSON.parse(localStorage.getItem('user') || '{}');
        this.todos = [];
        this.currentTheme = localStorage.getItem('theme') || 'light';
        this.currentLanguage = localStorage.getItem('language') || 'en';
        
        // Track which buttons have been bound to prevent duplicates
        this.buttonsBound = {
            addTodoBtn: false,
            languageBtn: false,
            themeBtn: false,
            logoutBtn: false
        };
        
        this.init();
    }
    
    init() {
        // Clear invalid tokens on startup if they exist
        if (this.token && (this.token === 'null' || this.token === 'undefined' || this.token === '')) {
            console.log('Clearing invalid token on startup');
            this.clearInvalidToken();
        }
        
        // Check authentication on page load
        if (window.location.pathname === '/auth') {
            if (this.isAuthenticated()) {
                console.log('Already authenticated, redirecting to todos');
                window.location.href = '/todos';
                return;
            }
            this.initAuth();
        } else if (window.location.pathname === '/todos') {
            if (!this.isAuthenticated()) {
                console.log('No valid JWT token found, redirecting to auth');
                this.clearInvalidToken(); // Ensure we clear any invalid data
                window.location.href = '/auth';
                return;
            }
            console.log('JWT token found, initializing todos page');
            this.initTodos();
        }
        
        // Apply current theme
        this.applyTheme();
        
        // Set up global event listeners
        this.setupGlobalEvents();
    }
    
    isAuthenticated() {
        return this.token && this.token !== 'null' && this.token !== 'undefined' && this.token.length > 0;
    }
    
    clearInvalidToken() {
        // Clear invalid or expired token
        console.log('Clearing invalid JWT token');
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user');
        this.token = null;
        this.user = {};
    }
    
    async apiCall(endpoint, options = {}) {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };
        
        if (this.token) {
            config.headers['Authorization'] = `Bearer ${this.token}`;
        }
        
        try {
            const response = await fetch(this.apiBase + endpoint, config);
            
            if (response.status === 401 || response.status === 403) {
                // Token expired or invalid - clear it and handle appropriately
                console.warn('Authentication failed for:', endpoint, 'Status:', response.status);
                this.clearInvalidToken();
                if (window.location.pathname === '/todos') {
                    // Only redirect to auth if we're on a protected page
                    window.location.href = '/auth';
                    return { success: false, data: { message: 'Authentication required' }, status: response.status };
                }
                return { success: false, data: { message: 'Authentication required' }, status: response.status };
            }
            
            const data = await response.json();
            return { success: response.ok, data, status: response.status };
        } catch (error) {
            console.error('API call failed:', error);
            return { success: false, error: error.message };
        }
    }
    
    // Authentication methods
    initAuth() {
        const loginForm = document.getElementById('loginForm');
        const registerForm = document.getElementById('registerForm');
        const toggleButton = document.getElementById('toggleAuth');
        
        if (loginForm) {
            loginForm.addEventListener('click', (e) => {
                if (e.target.type === 'submit' || e.target.tagName === 'BUTTON') {
                    e.preventDefault();
                    this.handleLogin(e);
                }
            });
        }
        
        if (registerForm) {
            registerForm.addEventListener('click', (e) => {
                if (e.target.type === 'submit' || e.target.tagName === 'BUTTON') {
                    e.preventDefault();
                    this.handleRegister(e);
                }
            });
        }
        
        if (toggleButton) {
            toggleButton.addEventListener('click', (e) => this.toggleAuthMode(e));
        }
        
        // Also listen for Enter key in form fields
        document.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && (e.target.name === 'username' || e.target.name === 'password' || e.target.name === 'email')) {
                e.preventDefault();
                if (document.getElementById('loginForm') && !document.getElementById('registerForm')) {
                    this.handleLogin(e);
                } else if (document.getElementById('registerForm')) {
                    this.handleRegister(e);
                }
            }
        });
    }
    
    async handleLogin(e) {
        e.preventDefault();
        
        // Get form values from the form elements
        const usernameInput = document.querySelector('#loginForm input[name="username"]');
        const passwordInput = document.querySelector('#loginForm input[name="password"]');
        
        if (!usernameInput || !passwordInput) {
            this.showError('Username and password fields are required');
            return;
        }
        
        const credentials = {
            username: usernameInput.value.trim(),
            password: passwordInput.value
        };
        
        console.log('Login attempt with username:', credentials.username);
        
        if (!credentials.username || !credentials.password) {
            this.showError('Please fill in all fields');
            return;
        }
        
        const result = await this.apiCall('/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials)
        });
        
        console.log('Login result:', result);
        
        if (result && result.success) {
            this.token = result.data.token;
            this.user = result.data.user;
            localStorage.setItem('jwt_token', this.token);
            localStorage.setItem('user', JSON.stringify(this.user));
            window.location.href = '/todos';
        } else {
            this.showError('Login failed: ' + (result?.data?.message || 'Unknown error'));
        }
    }
    
    async handleRegister(e) {
        e.preventDefault();
        
        // Get form values from the form elements
        const usernameInput = document.querySelector('#registerForm input[name="username"]');
        const emailInput = document.querySelector('#registerForm input[name="email"]');
        const passwordInput = document.querySelector('#registerForm input[name="password"]');
        
        if (!usernameInput || !emailInput || !passwordInput) {
            this.showError('All fields are required for registration');
            return;
        }
        
        const userData = {
            username: usernameInput.value.trim(),
            email: emailInput.value.trim(),
            password: passwordInput.value
        };
        
        console.log('Registration attempt with username:', userData.username);
        
        if (!userData.username || !userData.email || !userData.password) {
            this.showError('Please fill in all fields');
            return;
        }
        
        const result = await this.apiCall('/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
        
        console.log('Registration result:', result);
        
        if (result && result.success) {
            this.token = result.data.token;
            this.user = result.data.user;
            localStorage.setItem('jwt_token', this.token);
            localStorage.setItem('user', JSON.stringify(this.user));
            window.location.href = '/todos';
        } else {
            this.showError('Registration failed: ' + (result?.data?.message || 'Unknown error'));
        }
    }
    
    logout() {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user');
        this.token = null;
        this.user = {};
        window.location.href = '/auth';
    }
    
    // Todo methods
    async initTodos() {
        await this.loadTodos();
        this.setupTodoEvents();
        this.updateUserInfo();
    }
    
    async loadTodos() {
        const result = await this.apiCall('/todos');
        if (result && result.success) {
            this.todos = result.data.todos || [];
            this.renderTodos();
        }
    }
    
    async addTodo(text) {
        if (!text.trim()) return;
        
        const result = await this.apiCall('/todos', {
            method: 'POST',
            body: JSON.stringify({ text: text.trim() })
        });
        
        if (result && result.success) {
            // Reload the page to show the new todo (server-side rendering)
            window.location.reload();
        } else {
            this.showError('Failed to add todo');
        }
    }
    
    async toggleTodo(id) {
        const todo = this.todos.find(t => t.id === id);
        if (!todo) return;
        
        const result = await this.apiCall(`/todos/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ completed: !todo.completed })
        });
        
        if (result && result.success) {
            // Reload the page to show the updated todo (server-side rendering)
            window.location.reload();
        }
    }
    
    async deleteTodo(id) {
        const result = await this.apiCall(`/todos/${id}`, {
            method: 'DELETE'
        });
        
        if (result && result.success) {
            // Reload the page to show the updated todos (server-side rendering)
            window.location.reload();
        }
    }
    
    async clearCompleted() {
        const result = await this.apiCall('/todos/completed', {
            method: 'DELETE'
        });
        
        if (result && result.success) {
            // Reload the page to show the updated todos (server-side rendering)
            window.location.reload();
        }
    }
    
    setupTodoEvents() {
        // All button clicks are now handled by document-level event delegation
        // Just set up Enter key handler for todo input
        const todoInput = document.getElementById('newTodoInput');
        if (todoInput) {
            console.log('Setting up Enter key handler for todo input');
            todoInput.addEventListener('keypress', async (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    if (todoInput.value.trim()) {
                        await this.addTodo(todoInput.value.trim());
                        todoInput.value = '';
                    }
                }
            });
        }
        
        console.log('Todo events setup complete - using document-level delegation for buttons');
    }
    
    renderTodos() {
        // Server-side rendering handles the initial todos display
        // No need to reload the page since todos are already rendered
        console.log('Todos loaded successfully:', this.todos.length, 'items');
    }
    
    updateUserInfo() {
        const usernameEl = document.getElementById('username');
        if (usernameEl && this.user.username) {
            usernameEl.textContent = this.user.username;
        }
    }
    
    // Theme and Language methods
    async toggleTheme() {
        this.currentTheme = this.currentTheme === 'light' ? 'dark' : 'light';
        localStorage.setItem('theme', this.currentTheme);
        console.log('Theme toggled to:', this.currentTheme);
        
        // Apply theme immediately
        this.applyTheme();
        
        // Only update user settings via API if authenticated
        if (this.isAuthenticated()) {
            console.log('Updating theme setting for authenticated user');
            const result = await this.updateUserSettings({
                theme: this.currentTheme
            });
            
            if (!result || !result.success) {
                console.warn('Failed to update theme setting on server');
                return;
            }
            
            console.log('Theme setting updated successfully on server');
        } else {
            console.log('Theme applied locally (user not authenticated)');
        }
    }
    
    async toggleLanguage() {
        const languages = ['en', 'es', 'fr'];
        const currentIndex = languages.indexOf(this.currentLanguage);
        this.currentLanguage = languages[(currentIndex + 1) % languages.length];
        localStorage.setItem('language', this.currentLanguage);
        console.log('Language toggled to:', this.currentLanguage);
        
        // Only update user settings via API if authenticated
        if (this.isAuthenticated()) {
            console.log('Updating language setting for authenticated user');
            const result = await this.updateUserSettings({
                language: this.currentLanguage
            });
            
            if (!result || !result.success) {
                console.warn('Failed to update language setting on server');
                // Don't reload if the API call failed
                return;
            }
            
            // Only reload if API call was successful
            setTimeout(() => {
                window.location.reload();
            }, 100);
        } else {
            console.log('Language changed to:', this.currentLanguage, '(local only - please log in to persist)');
        }
    }
    
    async updateUserSettings(settings) {
        if (!this.isAuthenticated()) {
            console.warn('Cannot update user settings: not authenticated');
            return { success: false, message: 'Not authenticated' };
        }
        
        const result = await this.apiCall('/auth/settings', {
            method: 'PUT',
            body: JSON.stringify(settings)
        });
        
        if (result && result.success) {
            // Update local user object
            this.user = { ...this.user, ...settings };
            localStorage.setItem('user', JSON.stringify(this.user));
            console.log('User settings updated successfully');
        } else {
            console.error('Failed to update user settings:', result?.data?.message || result?.error || 'Unknown error');
        }
        
        return result;
    }
    
    applyTheme() {
        document.body.classList.toggle('dark-theme', this.currentTheme === 'dark');
        document.body.classList.toggle('light-theme', this.currentTheme === 'light');
    }
    
    setupGlobalEvents() {
        // Document-level click handler for all framework buttons and todo actions
        document.addEventListener('click', (e) => {
            // Handle todo item events using event delegation
            if (e.target.classList.contains('todo-toggle')) {
                const id = parseInt(e.target.dataset.todoId);
                this.toggleTodo(id);
                return;
            } else if (e.target.classList.contains('todo-delete')) {
                const id = parseInt(e.target.dataset.todoId);
                this.deleteTodo(id);
                return;
            }
            
            // Handle framework buttons with data-onclick-action attribute
            if (e.target.hasAttribute('data-onclick-action') || e.target.hasAttribute('data-onclick-id')) {
                e.preventDefault();
                this.handleFrameworkButtonClick(e.target);
                return;
            }
        });

        console.log('Global event handlers set up');
    }
    
    handleFrameworkButtonClick(element) {
        const elementId = element.id;
        console.log('Framework button clicked:', elementId, element);
        
        // Map button IDs to their handlers
        switch (elementId) {
            case 'addTodoBtn':
                this.handleAddTodoClick();
                break;
            case 'languageBtn':
                console.log('Language toggle clicked');
                this.toggleLanguage();
                break;
            case 'themeBtn':
                console.log('Theme toggle clicked');
                this.toggleTheme();
                break;
            case 'logoutBtn':
                console.log('Logout clicked');
                this.logout();
                break;
            default:
                console.warn('Unknown button clicked:', elementId, element);
                break;
        }
    }
    
    async handleAddTodoClick() {
        const input = document.getElementById('newTodoInput');
        if (input && input.value.trim()) {
            console.log('Adding todo:', input.value.trim());
            await this.addTodo(input.value.trim());
            input.value = '';
        } else {
            console.log('Add todo: input is empty or not found');
        }
    }
    
    // Legacy method - no longer needed with document-level event delegation
    bindGlobalButtons(retryCount = 0) {
        console.log('Using document-level event delegation - no individual button binding needed');
    }
    
    showError(message) {
        // Simple error display - could be enhanced with better UI
        alert(message);
        console.error(message);
    }
    
    toggleAuthMode(e) {
        e.preventDefault();
        // This would toggle between login and register forms
        // For now, redirect to appropriate page
        const isLoginPage = window.location.search.includes('mode=login') || !window.location.search.includes('mode=register');
        const newMode = isLoginPage ? 'register' : 'login';
        window.location.href = `/auth?mode=${newMode}`;
    }
}

// Initialize the app when the DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new TodoApp();
});
*/

console.log('Custom JavaScript disabled - using Summon framework hydration instead');