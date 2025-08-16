package code.yousef.summon.examples.ktor.routing

import code.yousef.summon.examples.ktor.models.Language
import code.yousef.summon.examples.ktor.models.Theme
import code.yousef.summon.examples.ktor.models.UserSession
import code.yousef.summon.examples.ktor.pages.AuthPage
import code.yousef.summon.examples.ktor.pages.TodoPage
import code.yousef.summon.runtime.jvm.KtorRenderer
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*

fun Route.pageRoutes() {
    get("/") {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            // Redirect to auth page
            call.respondRedirect("/auth")
        } else {
            // Show todo page
            call.respondRedirect("/todos")
        }
    }
    
    get("/auth") {
        val session = call.sessions.get<UserSession>()
        val language = session?.language?.let { lang -> 
            Language.values().find { it.code == lang } 
        } ?: Language.ENGLISH
        val theme = session?.theme?.let { t -> 
            Theme.values().find { it.value == t } 
        } ?: Theme.LIGHT
        val isLogin = call.request.queryParameters["mode"] != "register"
        
        call.respondHtml {
            head {
                title("${if (isLogin) "Login" else "Register"} - Ktor Todo App")
                meta(charset = "UTF-8")
                meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                style {
                    unsafe {
                        raw("""
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body { 
                                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                                min-height: 100vh;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                            }
                        """.trimIndent())
                    }
                }
            }
            body {
                div {
                    id = "root"
                    // Render Summon AuthPage component
                    val renderer = KtorRenderer()
                    unsafe {
                        raw(renderer.renderToString {
                            AuthPage(language, theme, isLogin)
                        })
                    }
                }
                script {
                    unsafe {
                        raw("""
                            function submitAuth() {
                                const form = new FormData();
                                const isLogin = window.location.search.includes('mode=register') ? false : true;
                                
                                if (!isLogin) {
                                    form.append('email', document.querySelector('input[name="email"]').value);
                                }
                                form.append('username', document.querySelector('input[name="username"]').value);
                                form.append('password', document.querySelector('input[name="password"]').value);
                                
                                const endpoint = isLogin ? '/auth/login' : '/auth/register';
                                
                                fetch(endpoint, {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/json' },
                                    body: JSON.stringify(Object.fromEntries(form))
                                })
                                .then(response => response.json())
                                .then(data => {
                                    if (data.success) {
                                        window.location.href = '/todos';
                                    } else {
                                        alert(data.message);
                                    }
                                });
                            }
                            
                            function toggleAuthMode() {
                                const isLogin = !window.location.search.includes('mode=register');
                                const newMode = isLogin ? 'register' : 'login';
                                window.location.href = '/auth' + (newMode === 'register' ? '?mode=register' : '');
                            }
                        """.trimIndent())
                    }
                }
            }
        }
    }
    
    get("/todos") {
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respondRedirect("/auth")
            return@get
        }
        
        val language = Language.values().find { it.code == session.language } ?: Language.ENGLISH
        val theme = Theme.values().find { it.value == session.theme } ?: Theme.LIGHT
        
        call.respondHtml {
            head {
                title("Todo App - Ktor")
                meta(charset = "UTF-8")
                meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                style {
                    unsafe {
                        raw("""
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body { 
                                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                                min-height: 100vh;
                            }
                        """.trimIndent())
                    }
                }
            }
            body {
                div {
                    id = "root"
                    // Render Summon TodoPage component
                    val renderer = KtorRenderer()
                    unsafe {
                        raw(renderer.renderToString {
                            TodoPage(language, theme, session.username)
                        })
                    }
                }
                script {
                    unsafe {
                        raw("""
                            // WebSocket connection for real-time updates
                            let ws = null;
                            let todos = [];
                            let currentFilter = 'ALL';
                            
                            // Initialize WebSocket
                            function initWebSocket() {
                                const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                                ws = new WebSocket(protocol + '//' + window.location.host + '/ws');
                                
                                ws.onopen = function() {
                                    console.log('WebSocket connected');
                                    loadTodos();
                                };
                                
                                ws.onmessage = function(event) {
                                    const message = JSON.parse(event.data);
                                    handleWebSocketMessage(message);
                                };
                                
                                ws.onclose = function() {
                                    console.log('WebSocket disconnected');
                                    setTimeout(initWebSocket, 1000); // Reconnect
                                };
                            }
                            
                            function handleWebSocketMessage(message) {
                                if (message.TodoAdded) {
                                    todos.push(message.TodoAdded);
                                    renderTodos();
                                } else if (message.TodoUpdated) {
                                    const index = todos.findIndex(t => t.id === message.TodoUpdated.id);
                                    if (index !== -1) {
                                        todos[index] = message.TodoUpdated;
                                        renderTodos();
                                    }
                                } else if (message.TodoDeleted) {
                                    todos = todos.filter(t => t.id !== message.TodoDeleted);
                                    renderTodos();
                                }
                            }
                            
                            function loadTodos() {
                                fetch('/api/todos')
                                    .then(response => response.json())
                                    .then(data => {
                                        if (data.success) {
                                            todos = data.todos || [];
                                            renderTodos();
                                        }
                                    });
                            }
                            
                            function addTodo() {
                                const input = document.querySelector('input[name="newTodo"]');
                                const text = input.value.trim();
                                if (!text) return;
                                
                                fetch('/api/todos', {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/json' },
                                    body: JSON.stringify({ text })
                                })
                                .then(response => response.json())
                                .then(data => {
                                    if (data.success) {
                                        input.value = '';
                                    }
                                });
                            }
                            
                            function toggleTodo(id) {
                                const todo = todos.find(t => t.id === id);
                                if (!todo) return;
                                
                                fetch('/api/todos/' + id, {
                                    method: 'PUT',
                                    headers: { 'Content-Type': 'application/json' },
                                    body: JSON.stringify({ completed: !todo.completed })
                                });
                            }
                            
                            function deleteTodo(id) {
                                fetch('/api/todos/' + id, { method: 'DELETE' });
                            }
                            
                            function clearCompleted() {
                                fetch('/api/todos/completed', { method: 'DELETE' });
                            }
                            
                            function setFilter(filter) {
                                currentFilter = filter;
                                renderTodos();
                            }
                            
                            function logout() {
                                fetch('/auth/logout', { method: 'POST' })
                                    .then(() => window.location.href = '/auth');
                            }
                            
                            function renderTodos() {
                                // This would update the UI - simplified for this example
                                console.log('Rendering todos:', todos);
                            }
                            
                            // Initialize when page loads
                            document.addEventListener('DOMContentLoaded', initWebSocket);
                        """.trimIndent())
                    }
                }
            }
        }
    }
}