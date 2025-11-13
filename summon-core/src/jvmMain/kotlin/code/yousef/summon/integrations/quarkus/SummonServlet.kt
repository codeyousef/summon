package codes.yousef.summon.integration.quarkus

// import jakarta.servlet.annotation.WebServlet // Temporarily commented out
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * A simple servlet that directly renders HTML pages for Summon.
 * Currently disabled - can be enabled by uncommenting the @WebServlet annotation.
 */
// @WebServlet(name = "SummonServlet", urlPatterns = ["/servlet/*"])
class SummonServlet : HttpServlet() {

    /**
     * Handle GET requests for all Summon pages
     */
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.contentType = "text/html;charset=UTF-8"

        val requestPath = req.requestURI.removePrefix(req.contextPath)

        when {
            requestPath == "/" || requestPath == "/index.html" -> {
                renderHomePage(resp)
            }

            requestPath == "/hello" -> {
                renderHelloPage(resp)
            }

            requestPath == "/component" -> {
                renderComponentPage(resp)
            }

            requestPath == "/dashboard" -> {
                renderDashboardPage(resp)
            }

            requestPath == "/theme" -> {
                renderThemePage(resp)
            }

            requestPath == "/chat" -> {
                renderChatPage(resp)
            }

            else -> {
                renderHomePage(resp)
            }
        }
    }

    /**
     * Render the home page with Summon demo content
     */
    private fun renderHomePage(resp: HttpServletResponse) {
        val writer = resp.writer
        writer.println(
            """
        <!DOCTYPE html>
        <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta name="description" content="A demonstration of Summon with Quarkus">
                <title>Summon with Quarkus - Home</title>
                <style>
                    :root {
                      --primary-color: #4695EB;
                      --secondary-color: #FF4081;
                      --background-color: #FFFFFF;
                      --text-color: #333333;
                      --success-color: #4CAF50;
                      --error-color: #F44336;
                      --warning-color: #FF9800;
                      --info-color: #2196F3;
                    }
                    body {
                      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                      line-height: 1.6;
                      color: var(--text-color);
                      background-color: var(--background-color);
                      margin: 0;
                      padding: 20px;
                    }
                    .container {
                      max-width: 1200px;
                      margin: 0 auto;
                      padding: 0 20px;
                    }
                    .row {
                      display: flex;
                      flex-wrap: wrap;
                      margin: 0 -15px;
                    }
                    .col {
                      flex: 1;
                      padding: 0 15px;
                    }
                    .card {
                      background: white;
                      border-radius: 8px;
                      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                      padding: 24px;
                      margin-bottom: 24px;
                      transition: transform 0.3s ease;
                    }
                    .card:hover {
                      transform: translateY(-5px);
                    }
                    .btn {
                      display: inline-block;
                      background-color: var(--primary-color);
                      color: white;
                      border: none;
                      border-radius: 4px;
                      padding: 8px 16px;
                      cursor: pointer;
                      transition: background-color 0.3s;
                      font-size: 1rem;
                    }
                    .btn:hover {
                      background-color: #3a85d8;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Welcome to Summon with Quarkus</h1>
                    <p>This is the home page of the Summon Quarkus integration example.</p>
                    <p>Summon makes it easy to build interactive web UIs with Kotlin.</p>
                    <div class="row">
                        <div class="col">
                            <div class="card">
                                <h2>Server-Side Rendering</h2>
                                <p>Summon provides powerful server-side rendering capabilities.</p>
                                <button class="btn" onclick="window.location.href='/hello'">Try Examples</button>
                            </div>
                        </div>
                        <div class="col">
                            <div class="card">
                                <h2>Component-Based</h2>
                                <p>Create reusable UI components with Kotlin.</p>
                                <button class="btn" onclick="window.location.href='/component'">View Components</button>
                            </div>
                        </div>
                    </div>
                </div>
                <script src="https://unpkg.com/htmx.org@1.9.12"></script>
            </body>
        </html>
        """.trimIndent()
        )
    }

    /**
     * Render the hello example page
     */
    private fun renderHelloPage(resp: HttpServletResponse) {
        val writer = resp.writer
        writer.println(
            """
        <!DOCTYPE html>
        <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta name="description" content="A demonstration of Summon with Quarkus">
                <title>Hello from Summon</title>
                <style>
                    :root {
                      --primary-color: #4695EB;
                      --secondary-color: #FF4081;
                      --background-color: #FFFFFF;
                      --text-color: #333333;
                    }
                    body {
                      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                      line-height: 1.6;
                      color: var(--text-color);
                      background-color: var(--background-color);
                      margin: 0;
                      padding: 20px;
                    }
                    .container {
                      max-width: 800px;
                      margin: 0 auto;
                      padding: 20px;
                      text-align: center;
                    }
                    h1 {
                      color: var(--primary-color);
                    }
                    button {
                      background-color: var(--primary-color);
                      color: white;
                      border: none;
                      border-radius: 4px;
                      padding: 8px 16px;
                      cursor: pointer;
                      transition: background-color 0.3s;
                      font-size: 1rem;
                    }
                    button:hover {
                      background-color: #3a85d8;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Hello from Summon!</h1>
                    <p>This is a simple example of Summon rendering in Quarkus</p>
                    <button onclick="alert('Button clicked!')">Click Me</button>
                    <p><a href="/">Back to Home</a></p>
                </div>
                <script src="https://unpkg.com/htmx.org@1.9.12"></script>
            </body>
        </html>
        """.trimIndent()
        )
    }

    /**
     * Render the component example page
     */
    private fun renderComponentPage(resp: HttpServletResponse) {
        val writer = resp.writer
        writer.println(
            """
        <!DOCTYPE html>
        <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Summon Components</title>
                <style>
                    body {
                      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                      line-height: 1.6;
                      padding: 20px;
                    }
                    .component-container {
                      max-width: 800px;
                      margin: 40px auto;
                      padding: 20px;
                      border: 1px solid #eee;
                      border-radius: 8px;
                      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    h1 {
                      color: #4695EB;
                    }
                    button {
                      background-color: #4695EB;
                      color: white;
                      border: none;
                      border-radius: 4px;
                      padding: 8px 16px;
                      cursor: pointer;
                      transition: background-color 0.3s;
                      font-size: 1rem;
                      margin: 10px 0;
                    }
                    button:hover {
                      background-color: #3a85d8;
                    }
                </style>
            </head>
            <body>
                <div class="component-container">
                    <h1>Hello from Summon!</h1>
                    <p>This is a simple example of Summon rendering in Quarkus</p>
                    <button onclick="alert('Button clicked!')">Click Me</button>
                    <p><a href="/">Back to Home</a></p>
                </div>
                <script src="https://unpkg.com/htmx.org@1.9.12"></script>
            </body>
        </html>
        """.trimIndent()
        )
    }

    /**
     * Render the dashboard page
     */
    private fun renderDashboardPage(resp: HttpServletResponse) {
        val writer = resp.writer
        writer.println(
            """
        <!DOCTYPE html>
        <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Dashboard</title>
                <style>
                    :root {
                      --primary-color: #4695EB;
                      --background-color: #f5f7f9;
                      --text-color: #333333;
                    }
                    body {
                      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                      line-height: 1.6;
                      color: var(--text-color);
                      background-color: var(--background-color);
                      margin: 0;
                      padding: 20px;
                    }
                    .container {
                      max-width: 1200px;
                      margin: 0 auto;
                      padding: 20px;
                    }
                    h1 {
                      color: var(--text-color);
                    }
                    .card {
                      background: white;
                      border-radius: 8px;
                      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                      padding: 20px;
                      margin-bottom: 20px;
                    }
                    .nav {
                      background-color: var(--primary-color);
                      color: white;
                      padding: 1rem;
                      margin-bottom: 1rem;
                      border-radius: 8px;
                    }
                    .nav-list {
                      display: flex;
                      list-style-type: none;
                      margin: 0;
                      padding: 0;
                    }
                    .nav-item {
                      margin-right: 1.5rem;
                    }
                    .nav-link {
                      color: white;
                      text-decoration: none;
                      font-weight: 500;
                    }
                    .nav-link:hover {
                      text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <nav class="nav">
                        <ul class="nav-list">
                            <li class="nav-item"><a class="nav-link" href="/">Home</a></li>
                            <li class="nav-item"><a class="nav-link" href="/dashboard">Dashboard</a></li>
                            <li class="nav-item"><a class="nav-link" href="/theme">Theme</a></li>
                            <li class="nav-item"><a class="nav-link" href="/chat">Chat</a></li>
                        </ul>
                    </nav>
                    
                    <h1>Dashboard Page</h1>
                    
                    <div class="card">
                        <h2>Servlet-Rendered Dashboard</h2>
                        <p>This dashboard is served directly from the SummonServlet class.</p>
                        <p>If you can see this page, the direct HTML rendering from servlet is working correctly.</p>
                    </div>
                </div>
                <script src="https://unpkg.com/htmx.org@1.9.12"></script>
            </body>
        </html>
        """.trimIndent()
        )
    }

    /**
     * Render the theme page
     */
    private fun renderThemePage(resp: HttpServletResponse) {
        val writer = resp.writer
        writer.println(
            """
        <!DOCTYPE html>
        <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Theme</title>
                <style>
                    :root {
                      --primary-color: #4695EB;
                      --background-color: #f5f7f9;
                      --text-color: #333333;
                    }
                    body {
                      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                      line-height: 1.6;
                      color: var(--text-color);
                      background-color: var(--background-color);
                      margin: 0;
                      padding: 20px;
                    }
                    .container {
                      max-width: 1200px;
                      margin: 0 auto;
                      padding: 20px;
                    }
                    h1 {
                      color: var(--text-color);
                    }
                    .card {
                      background: white;
                      border-radius: 8px;
                      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                      padding: 20px;
                      margin-bottom: 20px;
                    }
                    .nav {
                      background-color: var(--primary-color);
                      color: white;
                      padding: 1rem;
                      margin-bottom: 1rem;
                      border-radius: 8px;
                    }
                    .nav-list {
                      display: flex;
                      list-style-type: none;
                      margin: 0;
                      padding: 0;
                    }
                    .nav-item {
                      margin-right: 1.5rem;
                    }
                    .nav-link {
                      color: white;
                      text-decoration: none;
                      font-weight: 500;
                    }
                    .nav-link:hover {
                      text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <nav class="nav">
                        <ul class="nav-list">
                            <li class="nav-item"><a class="nav-link" href="/">Home</a></li>
                            <li class="nav-item"><a class="nav-link" href="/dashboard">Dashboard</a></li>
                            <li class="nav-item"><a class="nav-link" href="/theme">Theme</a></li>
                            <li class="nav-item"><a class="nav-link" href="/chat">Chat</a></li>
                        </ul>
                    </nav>
                    
                    <h1>Theme Page</h1>
                    
                    <div class="card">
                        <h2>Servlet-Rendered Theme</h2>
                        <p>This theme page is served directly from the SummonServlet class.</p>
                        <p>If you can see this page, the direct HTML rendering from servlet is working correctly.</p>
                    </div>
                </div>
                <script src="https://unpkg.com/htmx.org@1.9.12"></script>
            </body>
        </html>
        """.trimIndent()
        )
    }

    /**
     * Render the chat page
     */
    private fun renderChatPage(resp: HttpServletResponse) {
        val writer = resp.writer
        writer.println(
            """
        <!DOCTYPE html>
        <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Chat</title>
                <style>
                    :root {
                      --primary-color: #4695EB;
                      --background-color: #f5f7f9;
                      --text-color: #333333;
                    }
                    body {
                      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                      line-height: 1.6;
                      color: var(--text-color);
                      background-color: var(--background-color);
                      margin: 0;
                      padding: 20px;
                    }
                    .container {
                      max-width: 1200px;
                      margin: 0 auto;
                      padding: 20px;
                    }
                    h1 {
                      color: var(--text-color);
                    }
                    .card {
                      background: white;
                      border-radius: 8px;
                      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                      padding: 20px;
                      margin-bottom: 20px;
                    }
                    .nav {
                      background-color: var(--primary-color);
                      color: white;
                      padding: 1rem;
                      margin-bottom: 1rem;
                      border-radius: 8px;
                    }
                    .nav-list {
                      display: flex;
                      list-style-type: none;
                      margin: 0;
                      padding: 0;
                    }
                    .nav-item {
                      margin-right: 1.5rem;
                    }
                    .nav-link {
                      color: white;
                      text-decoration: none;
                      font-weight: 500;
                    }
                    .nav-link:hover {
                      text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <nav class="nav">
                        <ul class="nav-list">
                            <li class="nav-item"><a class="nav-link" href="/">Home</a></li>
                            <li class="nav-item"><a class="nav-link" href="/dashboard">Dashboard</a></li>
                            <li class="nav-item"><a class="nav-link" href="/theme">Theme</a></li>
                            <li class="nav-item"><a class="nav-link" href="/chat">Chat</a></li>
                        </ul>
                    </nav>
                    
                    <h1>Chat Page</h1>
                    
                    <div class="card">
                        <h2>Servlet-Rendered Chat</h2>
                        <p>This chat page is served directly from the SummonServlet class.</p>
                        <p>If you can see this page, the direct HTML rendering from servlet is working correctly.</p>
                    </div>
                </div>
                <script src="https://unpkg.com/htmx.org@1.9.12"></script>
            </body>
        </html>
        """.trimIndent()
        )
    }
} 