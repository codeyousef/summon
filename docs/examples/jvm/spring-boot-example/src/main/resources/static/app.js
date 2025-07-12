// Spring Boot Summon Example - Client-side JavaScript

// Utility functions for the application
window.SummonApp = {
    // Initialize the application
    init: function() {
        console.log('Spring Boot Summon Example loaded');
        this.setupGlobalErrorHandling();
        this.setupLiveUpdates();
    },

    // Setup global error handling
    setupGlobalErrorHandling: function() {
        window.handleApiError = function(error, fallbackMessage = 'An error occurred') {
            console.error('API Error:', error);
            const message = error.message || fallbackMessage;
            
            const alert = document.createElement('div');
            alert.className = 'alert alert-danger alert-dismissible fade show position-fixed top-0 end-0 m-3';
            alert.style.zIndex = '9999';
            alert.innerHTML = `
                <strong>Error:</strong> ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            document.body.appendChild(alert);
            
            setTimeout(() => {
                if (alert.classList.contains('show')) {
                    alert.classList.remove('show');
                    setTimeout(() => alert.remove(), 150);
                }
            }, 5000);
        };
    },

    // Setup live updates
    setupLiveUpdates: function() {
        // Update time every second if time element exists
        setInterval(() => {
            const timeElements = document.querySelectorAll('#current-time, #header-time');
            if (timeElements.length > 0) {
                this.updateTime();
            }
        }, 1000);
    },

    // Update time from server
    updateTime: function() {
        fetch('/api/time')
            .then(response => response.text())
            .then(time => {
                document.querySelectorAll('#current-time, #header-time').forEach(element => {
                    element.textContent = time;
                    element.classList.add('updated');
                    setTimeout(() => element.classList.remove('updated'), 300);
                });
            })
            .catch(error => console.log('Error updating time:', error));
    },

    // Counter functions
    updateCounter: function(action) {
        const endpoint = action === 'reset' ? '/api/counter/reset' : `/api/counter/${action}`;
        
        fetch(endpoint, { method: 'POST' })
            .then(response => response.json())
            .then(data => {
                const counterElement = document.getElementById('counter-value');
                if (counterElement) {
                    counterElement.textContent = data.value;
                    this.animateCounterChange(counterElement);
                }
            })
            .catch(error => {
                console.error('Error updating counter:', error);
                window.handleApiError(error, 'Failed to update counter');
            });
    },

    // Animate counter changes
    animateCounterChange: function(element) {
        element.classList.add('changed');
        setTimeout(() => element.classList.remove('changed'), 200);
    },

    // User management functions
    editUser: function(userId) {
        fetch(`/api/users/${userId}`)
            .then(response => response.json())
            .then(user => {
                document.getElementById('editUserId').value = user.id;
                document.getElementById('editName').value = user.name;
                document.getElementById('editEmail').value = user.email;
                document.getElementById('editRole').value = user.role;
                
                const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
                modal.show();
            })
            .catch(error => {
                console.error('Error loading user:', error);
                window.handleApiError(error, 'Failed to load user data');
            });
    },

    deleteUser: function(userId, userName) {
        if (confirm(`Are you sure you want to delete user "${userName}"?`)) {
            fetch(`/api/users/${userId}`, { method: 'DELETE' })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        throw new Error('Failed to delete user');
                    }
                })
                .catch(error => {
                    console.error('Error deleting user:', error);
                    window.handleApiError(error, 'Failed to delete user');
                });
        }
    },

    toggleUserStatus: function(userId, isActive) {
        const action = isActive ? 'deactivate' : 'activate';
        
        fetch(`/api/users/${userId}/${action}`, { method: 'POST' })
            .then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    throw new Error(`Failed to ${action} user`);
                }
            })
            .catch(error => {
                console.error(`Error ${action}ing user:`, error);
                window.handleApiError(error, `Failed to ${action} user`);
            });
    },

    // Chat functionality
    initChat: function() {
        let socket;
        let username;

        window.connect = function() {
            const usernameInput = document.getElementById('username');
            username = usernameInput.value.trim();
            
            if (!username) {
                alert('Please enter a username');
                return;
            }

            // Connect to WebSocket
            const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            socket = new WebSocket(`${wsProtocol}//${window.location.host}/chat`);
            
            document.getElementById('login-section').style.display = 'none';
            document.getElementById('chat-section').style.display = 'block';
            document.getElementById('connection-status').textContent = 'Connecting...';
            
            socket.onopen = function() {
                document.getElementById('connection-status').textContent = 'Connected';
                SummonApp.addSystemMessage(`Welcome to the chat, ${username}!`);
            };

            socket.onmessage = function(event) {
                const message = JSON.parse(event.data);
                SummonApp.addMessage(message.username, message.message, message.username === username);
            };

            socket.onclose = function() {
                document.getElementById('connection-status').textContent = 'Disconnected';
                SummonApp.addSystemMessage('Disconnected from chat server');
            };

            socket.onerror = function(error) {
                console.error('WebSocket error:', error);
                SummonApp.addSystemMessage('Connection error occurred');
            };
        };

        window.disconnect = function() {
            if (socket) {
                socket.close();
            }
            document.getElementById('login-section').style.display = 'block';
            document.getElementById('chat-section').style.display = 'none';
            document.getElementById('chat-messages').innerHTML = '<div class="text-center text-muted"><p>Welcome to the chat! Messages will appear here.</p></div>';
        };

        window.sendMessage = function() {
            const messageInput = document.getElementById('messageInput');
            const message = messageInput.value.trim();
            
            if (message && socket && socket.readyState === WebSocket.OPEN) {
                socket.send(message);
                messageInput.value = '';
            }
        };

        window.handleKeyPress = function(event) {
            if (event.key === 'Enter') {
                sendMessage();
            }
        };
    },

    addMessage: function(sender, message, isOwn = false) {
        const chatMessages = document.getElementById('chat-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = `message${isOwn ? ' own' : ''}`;
        
        const bubbleDiv = document.createElement('div');
        bubbleDiv.className = 'message-bubble';
        bubbleDiv.innerHTML = `<strong>${sender}:</strong> ${message}`;
        
        messageDiv.appendChild(bubbleDiv);
        chatMessages.appendChild(messageDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    },

    addSystemMessage: function(message) {
        const chatMessages = document.getElementById('chat-messages');
        const messageDiv = document.createElement('div');
        messageDiv.className = 'text-center text-muted mb-2';
        messageDiv.innerHTML = `<small><em>${message}</em></small>`;
        
        chatMessages.appendChild(messageDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }
};

// Initialize the application when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    SummonApp.init();
    
    // Initialize chat if on chat page
    if (document.getElementById('chat-section')) {
        SummonApp.initChat();
    }
    
    // Auto-dismiss alerts
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert.classList.contains('show')) {
                alert.classList.remove('show');
                setTimeout(() => alert.remove(), 150);
            }
        }, 5000);
    });
});

// Expose functions globally for onclick handlers
window.updateCounter = SummonApp.updateCounter.bind(SummonApp);
window.editUser = SummonApp.editUser.bind(SummonApp);
window.deleteUser = SummonApp.deleteUser.bind(SummonApp);
window.toggleUserStatus = SummonApp.toggleUserStatus.bind(SummonApp);