package codes.yousef.summon.integration.quarkus

import jakarta.annotation.PostConstruct
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.logging.Logger
import java.net.URI

/**
 * Example Quarkus REST resource that demonstrates Summon server-side rendering
 */
@Path("/")
class SummonResource {

    private val logger = Logger.getLogger(SummonResource::class.java)

    @Inject
    lateinit var renderer: QuarkusExtension.SummonRenderer

    @PostConstruct
    fun init() {
        val port = System.getProperty("quarkus.http.port") ?: "unknown"
        System.out.println("************************************************************")
        System.out.println("** SUMMON RESOURCE INITIALIZED - PORT CONFIGURED AS: $port **")
        System.out.println("************************************************************")

        try {
            // Try to create a test log file directly
            val logFile = java.io.File("D:/Projects/KMP/summon/direct-init.log")
            logFile.writeText("SummonResource initialized at: ${java.time.LocalDateTime.now()}\n")
            logFile.appendText("Port configured: $port\n")
            System.out.println("Direct log file created at: ${logFile.absolutePath}")
        } catch (e: Exception) {
            System.out.println("Failed to create direct log file: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Simple ping test endpoint
     */
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    fun ping(): String {
        System.out.println("PING ENDPOINT CALLED")
        return "Ping successful! The JAX-RS endpoint is working."
    }

    /**
     * Dashboard fallback test endpoint that returns plain text
     */
    @GET
    @Path("/dashboard-test")
    @Produces(MediaType.TEXT_PLAIN)
    fun dashboardTest(): String {
        System.out.println("DASHBOARD-TEST ENDPOINT CALLED")
        return "Dashboard test successful! This endpoint works."
    }

    /**
     * Theme fallback test endpoint that returns plain text
     */
    @GET
    @Path("/theme-test")
    @Produces(MediaType.TEXT_PLAIN)
    fun themeTest(): String {
        System.out.println("THEME-TEST ENDPOINT CALLED")
        return "Theme test successful! This endpoint works."
    }

    /**
     * Chat fallback test endpoint that returns plain text
     */
    @GET
    @Path("/chat-test")
    @Produces(MediaType.TEXT_PLAIN)
    fun chatTest(): String {
        System.out.println("CHAT-TEST ENDPOINT CALLED")
        return "Chat test successful! This endpoint works."
    }

    /**
     * Endpoint that returns a fully rendered HTML page
     */
    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_HTML)
    fun hello(): String {
        System.out.println("**************** HELLO ENDPOINT CALLED ****************")
        logger.info("Calling /hello endpoint")
        val heading = renderer.renderHeading(1, "Hello from Summon!")
        val paragraph = renderer.renderParagraph("This is a simple example of Summon rendering in Quarkus")
        val button = renderer.renderButton("Click Me", "alert('Button clicked!')")

        val content = "$heading\n$paragraph\n$button"
        return renderer.renderTemplate("Hello from Summon", content)
    }

    /**
     * Endpoint that returns just a component fragment
     */
    @GET
    @Path("/component")
    @Produces(MediaType.TEXT_HTML)
    fun component(): String {
        System.out.println("**************** COMPONENT ENDPOINT CALLED ****************")
        logger.info("Calling /component endpoint")
        val heading = renderer.renderHeading(1, "Hello from Summon!")
        val paragraph = renderer.renderParagraph("This is a simple example of Summon rendering in Quarkus")
        val button = renderer.renderButton("Click Me", "alert('Button clicked!')")

        return "$heading\n$paragraph\n$button"
    }

    /**
     * Home page endpoint at root path with custom mapping
     */
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        System.out.println("**************** HOME ENDPOINT CALLED ****************")
        logger.debug(">>>>> DEBUGGING: Entering / (home) endpoint")
        logger.info("Calling / (home) endpoint")
        logger.warn("If you see this log message, logging is working correctly for the home page")
        val htmlContent = """
            <div class="container mx-auto px-4 py-8">
                <header class="mb-8">
                    <h1 class="text-3xl font-bold text-center">Summon Demo Application</h1>
                    <p class="text-center text-gray-600">Server-side rendering for Kotlin Multiplatform</p>
                </header>
                
                <nav class="mb-8">
                    <ul class="flex justify-center space-x-6">
                        <li><a href="/" class="text-blue-500 hover:text-blue-700" hx-get="/" hx-target="body" hx-swap="innerHTML">Home</a></li>
                        <li><a href="/dashboard" class="text-blue-500 hover:text-blue-700" hx-get="/dashboard" hx-target="body" hx-swap="innerHTML">Dashboard</a></li>
                        <li><a href="/theme" class="text-blue-500 hover:text-blue-700" hx-get="/theme" hx-target="body" hx-swap="innerHTML">Theme</a></li>
                        <li><a href="/chat" class="text-blue-500 hover:text-blue-700" hx-get="/chat" hx-target="body" hx-swap="innerHTML">Chat</a></li>
                    </ul>
                </nav>
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div class="bg-white p-6 rounded-lg shadow-md">
                        <h2 class="text-xl font-semibold mb-4">What is Summon?</h2>
                        <p class="text-gray-700">
                            Summon is a server-side rendering library for Kotlin Multiplatform that allows
                            you to create dynamic web applications with a single codebase.
                        </p>
                    </div>
                    
                    <div class="bg-white p-6 rounded-lg shadow-md">
                        <h2 class="text-xl font-semibold mb-4">Features</h2>
                        <ul class="list-disc list-inside text-gray-700">
                            <li>Server-side rendering</li>
                            <li>Kotlin Multiplatform compatibility</li>
                            <li>Easy integration with Quarkus</li>
                            <li>Lightweight and performant</li>
                        </ul>
                    </div>
                </div>
                
                <div class="mt-8 bg-white p-6 rounded-lg shadow-md">
                    <h2 class="text-xl font-semibold mb-4">Getting Started</h2>
                    <p class="text-gray-700 mb-4">
                        To get started with Summon, explore the demo pages linked in the navigation above.
                        Each page demonstrates different features and capabilities of the library.
                    </p>
                    <div class="text-center">
                        <a href="/dashboard" class="inline-block bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600" hx-get="/dashboard" hx-target="body" hx-swap="innerHTML">
                            View Dashboard Demo
                        </a>
                    </div>
                </div>
            </div>
        """

        return renderer.renderTemplate("Summon Demo - Home", htmlContent)
    }

    @GET
    @Path("/direct-test")
    @Produces(MediaType.TEXT_HTML)
    fun getDirectTest(): String {
        logger.info("Calling /direct-test endpoint")
        return """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Direct Test</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            padding: 20px;
                            max-width: 800px;
                            margin: 0 auto;
                            background-color: #f0f0f0;
                        }
                        h1 { color: #333; }
                        .box {
                            background-color: white;
                            border: 1px solid #ddd;
                            padding: 20px;
                            border-radius: 5px;
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <h1>Minimal Test Page</h1>
                    <div class="box">
                        <p>This is a simple test page served directly from the SummonResource.</p>
                        <p>If you can see this, HTML rendering is working correctly.</p>
                        <a href="/">Go to Home</a>
                    </div>
                </body>
            </html>
        """.trimIndent()
    }

    /**
     * Direct dashboard implementation that works
     */
    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    fun getDashboardDirect(): String {
        System.out.println("**************** DASHBOARD ENDPOINT CALLED ****************")
        logger.debug(">>>>> DEBUGGING: Entering /dashboard endpoint")
        logger.info("Calling /dashboard endpoint")
        logger.warn("If you see this log message, logging is working correctly for the dashboard page")
        logger.info("Starting HTML rendering...")

        val html = """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="description" content="A demonstration of Summon with Quarkus">
                    <title>Dashboard</title>
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
                          --light-gray: #f5f7f9;
                          --border-color: #ddd;
                        }
                        
                        body {
                          font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                          line-height: 1.6;
                          color: var(--text-color);
                          background-color: var(--light-gray);
                          margin: 0;
                          padding: 20px;
                        }
                        
                        .container {
                          max-width: 1200px;
                          margin: 0 auto;
                          padding: 0;
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
                        
                        .dashboard-header {
                          display: flex;
                          justify-content: space-between;
                          align-items: center;
                          margin-bottom: 1.5rem;
                        }
                        
                        .dashboard-title h1 {
                          margin: 0;
                          font-size: 1.8rem;
                          color: var(--text-color);
                        }
                        
                        .dashboard-controls {
                          display: flex;
                          gap: 10px;
                        }
                        
                        .dashboard-controls button {
                          padding: 8px 16px;
                          background-color: var(--background-color);
                          border: 1px solid var(--border-color);
                          border-radius: 4px;
                          cursor: pointer;
                          font-size: 0.9rem;
                          display: flex;
                          align-items: center;
                          gap: 5px;
                        }
                        
                        .dashboard-controls button:hover {
                          background-color: #f0f0f0;
                        }
                        
                        .dashboard-grid {
                          display: grid;
                          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
                          gap: 20px;
                          margin-bottom: 30px;
                        }
                        
                        .dashboard-card {
                          background-color: var(--background-color);
                          border-radius: 8px;
                          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                          padding: 20px;
                          transition: box-shadow 0.3s;
                        }
                        
                        .dashboard-card:hover {
                          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
                        }
                        
                        .card-header {
                          display: flex;
                          justify-content: space-between;
                          align-items: center;
                          margin-bottom: 15px;
                        }
                        
                        .card-title {
                          font-size: 1.1rem;
                          font-weight: 500;
                          margin: 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <nav class="nav">
                            <ul class="nav-list">
                                <li class="nav-item"><a class="nav-link" href="/" hx-get="/" hx-target="body" hx-swap="innerHTML">Home</a></li>
                                <li class="nav-item"><a class="nav-link" href="/dashboard" hx-get="/dashboard" hx-target="body" hx-swap="innerHTML">Dashboard</a></li>
                                <li class="nav-item"><a class="nav-link" href="/theme" hx-get="/theme" hx-target="body" hx-swap="innerHTML">Theme</a></li>
                                <li class="nav-item"><a class="nav-link" href="/chat" hx-get="/chat" hx-target="body" hx-swap="innerHTML">Chat</a></li>
                            </ul>
                        </nav>
                        
                        <div class="dashboard-header">
                            <div class="dashboard-title">
                                <h1>Dashboard</h1>
                            </div>
                            <div class="dashboard-controls">
                                <button>
                                    <span>Filter</span>
                                </button>
                                <button>
                                    <span>Export</span>
                                </button>
                            </div>
                        </div>
                        
                        <div class="dashboard-grid">
                            <div class="dashboard-card">
                                <div class="card-header">
                                    <h3 class="card-title">Total Users</h3>
                                </div>
                                <div class="card-content">
                                    <div style="font-size: 2rem; font-weight: bold;">1,254</div>
                                    <div style="color: #4CAF50; display: flex; align-items: center; margin-top: 10px;">
                                        <span>+12% from last month</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="dashboard-card">
                                <div class="card-header">
                                    <h3 class="card-title">Revenue</h3>
                                </div>
                                <div class="card-content">
                                    <div style="font-size: 2rem; font-weight: bold;">$8,354</div>
                                    <div style="color: #4CAF50; display: flex; align-items: center; margin-top: 10px;">
                                        <span>+5.3% from last month</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="dashboard-card">
                                <div class="card-header">
                                    <h3 class="card-title">Active Projects</h3>
                                </div>
                                <div class="card-content">
                                    <div style="font-size: 2rem; font-weight: bold;">32</div>
                                    <div style="color: #F44336; display: flex; align-items: center; margin-top: 10px;">
                                        <span>-2 from last month</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="dashboard-card">
                                <div class="card-header">
                                    <h3 class="card-title">Support Tickets</h3>
                                </div>
                                <div class="card-content">
                                    <div style="font-size: 2rem; font-weight: bold;">28</div>
                                    <div style="color: #4CAF50; display: flex; align-items: center; margin-top: 10px;">
                                        <span>-15% from last month</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="dashboard-card" style="margin-bottom: 30px;">
                            <div class="card-header">
                                <h3 class="card-title">Recent Activities</h3>
                            </div>
                            <div class="card-content">
                                <div style="margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #eee;">
                                    <div style="display: flex; justify-content: space-between;">
                                        <div>
                                            <div style="font-weight: 500;">Project XYZ created</div>
                                            <div style="font-size: 0.9rem; color: #666;">By John Doe</div>
                                        </div>
                                        <div style="font-size: 0.9rem; color: #666;">2 hours ago</div>
                                    </div>
                                </div>
                                <div style="margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #eee;">
                                    <div style="display: flex; justify-content: space-between;">
                                        <div>
                                            <div style="font-weight: 500;">New team member added</div>
                                            <div style="font-size: 0.9rem; color: #666;">By Jane Smith</div>
                                        </div>
                                        <div style="font-size: 0.9rem; color: #666;">3 hours ago</div>
                                    </div>
                                </div>
                                <div style="margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #eee;">
                                    <div style="display: flex; justify-content: space-between;">
                                        <div>
                                            <div style="font-weight: 500;">Client meeting scheduled</div>
                                            <div style="font-size: 0.9rem; color: #666;">By Mark Wilson</div>
                                        </div>
                                        <div style="font-size: 0.9rem; color: #666;">5 hours ago</div>
                                    </div>
                                </div>
                                <div>
                                    <div style="display: flex; justify-content: space-between;">
                                        <div>
                                            <div style="font-weight: 500;">Invoice paid</div>
                                            <div style="font-size: 0.9rem; color: #666;">By Acme Corp</div>
                                        </div>
                                        <div style="font-size: 0.9rem; color: #666;">1 day ago</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
                </body>
            </html>
        """.trimIndent()

        logger.info("Finished building HTML, returning content with length: ${html.length}")
        return html
    }

    /**
     * Direct theme implementation that works
     */
    @GET
    @Path("/theme")
    @Produces(MediaType.TEXT_HTML)
    fun getThemeDirect(): String {
        System.out.println("**************** THEME ENDPOINT CALLED ****************")
        logger.debug(">>>>> DEBUGGING: Entering /theme endpoint")
        logger.info("Calling /theme endpoint")
        logger.warn("If you see this log message, logging is working correctly for the theme page")
        return """
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="description" content="A demonstration of Summon with Quarkus">
                    <title>Theme Customizer</title>
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
                          --light-gray: #f0f2f5;
                          --border-color: #e0e0e0;
                        }
                        
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        }
                        
                        body {
                            background-color: var(--light-gray);
                            color: var(--text-color);
                            line-height: 1.6;
                        }
                        
                        .container {
                            max-width: 1200px;
                            margin: 0 auto;
                            padding: 20px;
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
                        
                        .header {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 30px;
                        }
                        
                        h1 {
                            font-size: 24px;
                            font-weight: 600;
                        }
                        
                        .theme-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
                            gap: 20px;
                            margin-bottom: 30px;
                        }
                        
                        .card {
                            background-color: var(--background-color);
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
                            padding: 20px;
                            transition: box-shadow 0.3s;
                        }
                        
                        .card:hover {
                            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                        }
                        
                        .color-theme {
                            position: relative;
                        }
                        
                        .theme-preview {
                            height: 140px;
                            border-radius: 6px;
                            margin-bottom: 15px;
                            position: relative;
                            overflow: hidden;
                        }
                        
                        .theme-header {
                            height: 40px;
                            display: flex;
                            align-items: center;
                            padding: 0 15px;
                        }
                        
                        .theme-sidebar {
                            position: absolute;
                            left: 0;
                            top: 40px;
                            bottom: 0;
                            width: 60px;
                        }
                        
                        .theme-content {
                            position: absolute;
                            left: 60px;
                            right: 0;
                            top: 40px;
                            bottom: 0;
                            padding: 10px;
                        }
                        
                        .theme-block {
                            height: 10px;
                            margin-bottom: 6px;
                            border-radius: 2px;
                        }
                        
                        .theme-name {
                            font-weight: 600;
                            margin-bottom: 5px;
                        }
                        
                        .theme-description {
                            font-size: 14px;
                            color: #666;
                            margin-bottom: 15px;
                        }
                        
                        .btn {
                            padding: 8px 16px;
                            background-color: var(--primary-color);
                            color: white;
                            border: none;
                            border-radius: 4px;
                            cursor: pointer;
                            font-weight: 500;
                            transition: background-color 0.2s;
                        }
                        
                        .btn:hover {
                            background-color: #3a85d8;
                        }
                        
                        .btn-outline {
                            background-color: transparent;
                            color: var(--primary-color);
                            border: 1px solid var(--primary-color);
                        }
                        
                        .btn-outline:hover {
                            background-color: rgba(70, 149, 235, 0.05);
                        }
                        
                        .settings-section {
                            margin-top: 30px;
                            background-color: var(--background-color);
                            border-radius: 8px;
                            padding: 20px;
                        }
                        
                        .settings-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
                            gap: 20px;
                        }
                        
                        .setting-item {
                            margin-bottom: 15px;
                        }
                        
                        .setting-label {
                            display: block;
                            margin-bottom: 8px;
                            font-weight: 500;
                        }
                        
                        .setting-input {
                            width: 100%;
                            padding: 8px 12px;
                            border: 1px solid var(--border-color);
                            border-radius: 4px;
                        }
                        
                        .toggle-switch {
                            position: relative;
                            display: inline-block;
                            width: 50px;
                            height: 24px;
                        }
                        
                        .toggle-switch input {
                            opacity: 0;
                            width: 0;
                            height: 0;
                        }
                        
                        .toggle-slider {
                            position: absolute;
                            cursor: pointer;
                            top: 0;
                            left: 0;
                            right: 0;
                            bottom: 0;
                            background-color: #ccc;
                            transition: .4s;
                            border-radius: 24px;
                        }
                        
                        .toggle-slider:before {
                            position: absolute;
                            content: "";
                            height: 16px;
                            width: 16px;
                            left: 4px;
                            bottom: 4px;
                            background-color: white;
                            transition: .4s;
                            border-radius: 50%;
                        }
                        
                        input:checked + .toggle-slider {
                            background-color: var(--primary-color);
                        }
                        
                        input:checked + .toggle-slider:before {
                            transform: translateX(26px);
                        }
                        
                        @media (max-width: 768px) {
                            .nav-list {
                                flex-direction: column;
                            }
                            .nav-item {
                                margin-right: 0;
                                margin-bottom: 5px;
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <nav class="nav">
                            <ul class="nav-list">
                                <li class="nav-item"><a class="nav-link" href="/" hx-get="/" hx-target="body" hx-swap="innerHTML">Home</a></li>
                                <li class="nav-item"><a class="nav-link" href="/dashboard" hx-get="/dashboard" hx-target="body" hx-swap="innerHTML">Dashboard</a></li>
                                <li class="nav-item"><a class="nav-link" href="/theme" hx-get="/theme" hx-target="body" hx-swap="innerHTML">Theme</a></li>
                                <li class="nav-item"><a class="nav-link" href="/chat" hx-get="/chat" hx-target="body" hx-swap="innerHTML">Chat</a></li>
                            </ul>
                        </nav>
                        
                        <header class="header">
                            <h1>Theme Customizer</h1>
                            <button class="btn">Save Theme</button>
                        </header>

                        <section>
                            <h2>Color Themes</h2>
                            <p>Choose a predefined color scheme or customize your own.</p>
                            <div class="theme-grid">
                                <!-- Theme 1: Default -->
                                <div class="card color-theme">
                                    <div class="theme-preview">
                                        <div class="theme-header" style="background-color: #4695EB; color: white;"></div>
                                        <div class="theme-sidebar" style="background-color: #ffffff;"></div>
                                        <div class="theme-content" style="background-color: #f5f5f5;">
                                            <div class="theme-block" style="background-color: #ffffff; width: 100%;"></div>
                                            <div class="theme-block" style="background-color: #ffffff; width: 80%;"></div>
                                            <div class="theme-block" style="background-color: #ffffff; width: 60%;"></div>
                                        </div>
                                    </div>
                                    <h3 class="theme-name">Default Theme</h3>
                                    <p class="theme-description">Standard blue theme with light surfaces.</p>
                                    <button class="btn">Apply Theme</button>
                                </div>

                                <!-- Theme 2: Dark -->
                                <div class="card color-theme">
                                    <div class="theme-preview">
                                        <div class="theme-header" style="background-color: #212121; color: #e0e0e0;"></div>
                                        <div class="theme-sidebar" style="background-color: #171717;"></div>
                                        <div class="theme-content" style="background-color: #303030;">
                                            <div class="theme-block" style="background-color: #424242; width: 100%;"></div>
                                            <div class="theme-block" style="background-color: #424242; width: 80%;"></div>
                                            <div class="theme-block" style="background-color: #424242; width: 60%;"></div>
                                        </div>
                                    </div>
                                    <h3 class="theme-name">Dark Mode</h3>
                                    <p class="theme-description">Easy on the eyes with dark surfaces and contrast.</p>
                                    <button class="btn btn-outline">Apply Theme</button>
                                </div>

                                <!-- Theme 3: Pink -->
                                <div class="card color-theme">
                                    <div class="theme-preview">
                                        <div class="theme-header" style="background-color: #FF4081; color: white;"></div>
                                        <div class="theme-sidebar" style="background-color: #ffffff;"></div>
                                        <div class="theme-content" style="background-color: #fce4ec;">
                                            <div class="theme-block" style="background-color: #ffffff; width: 100%;"></div>
                                            <div class="theme-block" style="background-color: #ffffff; width: 80%;"></div>
                                            <div class="theme-block" style="background-color: #ffffff; width: 60%;"></div>
                                        </div>
                                    </div>
                                    <h3 class="theme-name">Pink Accent</h3>
                                    <p class="theme-description">Vibrant pink theme with light background.</p>
                                    <button class="btn btn-outline">Apply Theme</button>
                                </div>
                            </div>
                        </section>

                        <section class="settings-section">
                            <h2>Custom Settings</h2>
                            <p>Fine-tune your interface preferences.</p>
                            <div class="settings-grid">
                                <div class="setting-item">
                                    <label class="setting-label">Primary Color</label>
                                    <input type="color" class="setting-input" value="#4695EB">
                                </div>
                                <div class="setting-item">
                                    <label class="setting-label">Secondary Color</label>
                                    <input type="color" class="setting-input" value="#FF4081">
                                </div>
                                <div class="setting-item">
                                    <label class="setting-label">Background Color</label>
                                    <input type="color" class="setting-input" value="#F5F5F5">
                                </div>
                                <div class="setting-item">
                                    <label class="setting-label">Text Color</label>
                                    <input type="color" class="setting-input" value="#333333">
                                </div>
                            </div>

                            <div class="settings-grid" style="margin-top: 20px;">
                                <div class="setting-item">
                                    <label class="setting-label">Font Size</label>
                                    <select class="setting-input">
                                        <option>Small</option>
                                        <option selected>Medium</option>
                                        <option>Large</option>
                                        <option>Extra Large</option>
                                    </select>
                                </div>
                                <div class="setting-item">
                                    <label class="setting-label">Border Radius</label>
                                    <select class="setting-input">
                                        <option>None</option>
                                        <option>Slight</option>
                                        <option selected>Medium</option>
                                        <option>Rounded</option>
                                    </select>
                                </div>
                                <div class="setting-item">
                                    <label class="setting-label">Layout Style</label>
                                    <select class="setting-input">
                                        <option selected>Standard</option>
                                        <option>Compact</option>
                                        <option>Comfortable</option>
                                    </select>
                                </div>
                            </div>

                            <div style="margin-top: 20px;">
                                <div class="setting-item" style="display: flex; justify-content: space-between; align-items: center;">
                                    <span class="setting-label" style="margin-bottom: 0;">Dark Mode</span>
                                    <label class="toggle-switch">
                                        <input type="checkbox">
                                        <span class="toggle-slider"></span>
                                    </label>
                                </div>
                                <div class="setting-item" style="display: flex; justify-content: space-between; align-items: center;">
                                    <span class="setting-label" style="margin-bottom: 0;">Animations</span>
                                    <label class="toggle-switch">
                                        <input type="checkbox" checked>
                                        <span class="toggle-slider"></span>
                                    </label>
                                </div>
                                <div class="setting-item" style="display: flex; justify-content: space-between; align-items: center;">
                                    <span class="setting-label" style="margin-bottom: 0;">High Contrast</span>
                                    <label class="toggle-switch">
                                        <input type="checkbox">
                                        <span class="toggle-slider"></span>
                                    </label>
                                </div>
                            </div>
                        </section>
                    </div>
                    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
                </body>
            </html>
        """.trimIndent()
    }

    /**
     * Direct chat implementation that works
     */
    @GET
    @Path("/chat")
    @Produces(MediaType.TEXT_HTML)
    fun getChatDirect(): String {
        System.out.println("**************** CHAT ENDPOINT CALLED ****************")
        logger.debug(">>>>> DEBUGGING: Entering /chat endpoint")
        logger.info("Calling /chat endpoint")
        logger.warn("If you see this log message, logging is working correctly for the chat page")
        return """
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="description" content="A demonstration of Summon with Quarkus">
                    <title>Chat</title>
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
                          --light-gray: #f0f2f5;
                          --border-color: #e0e0e0;
                        }
                        
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        }
                        
                        body {
                            background-color: var(--light-gray);
                            color: var(--text-color);
                            line-height: 1.6;
                        }
                        
                        .container {
                            max-width: 1200px;
                            margin: 0 auto;
                            padding: 20px;
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
                        
                        .chat-container {
                            display: flex;
                            height: calc(100vh - 150px);
                            min-height: 500px;
                            background-color: var(--background-color);
                            border-radius: 8px;
                            overflow: hidden;
                            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        }
                        
                        .chat-sidebar {
                            width: 250px;
                            background-color: #f5f5f5;
                            border-right: 1px solid var(--border-color);
                        }
                        
                        .sidebar-header {
                            padding: 15px;
                            border-bottom: 1px solid var(--border-color);
                            background-color: #f0f0f0;
                        }
                        
                        .sidebar-header h2 {
                            font-size: 16px;
                            margin: 0;
                        }
                        
                        .user-search {
                            padding: 10px 15px;
                            border-bottom: 1px solid var(--border-color);
                        }
                        
                        .search-input {
                            width: 100%;
                            padding: 8px 10px;
                            border: 1px solid #ddd;
                            border-radius: 4px;
                            font-size: 14px;
                        }
                        
                        .user-list {
                            list-style: none;
                            overflow-y: auto;
                            height: calc(100% - 120px);
                        }
                        
                        .user-item {
                            padding: 10px 15px;
                            border-bottom: 1px solid #eee;
                            cursor: pointer;
                            transition: background-color 0.2s;
                        }
                        
                        .user-item:hover {
                            background-color: #eaeaea;
                        }
                        
                        .user-item.active {
                            background-color: #e3f2fd;
                            border-left: 3px solid var(--primary-color);
                        }
                        
                        .user-info {
                            display: flex;
                            align-items: center;
                        }
                        
                        .user-avatar {
                            width: 40px;
                            height: 40px;
                            border-radius: 50%;
                            background-color: #ccc;
                            margin-right: 10px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-weight: bold;
                            color: white;
                        }
                        
                        .user-details {
                            flex: 1;
                        }
                        
                        .user-name {
                            font-weight: 500;
                            margin-bottom: 2px;
                        }
                        
                        .user-status {
                            font-size: 12px;
                            color: #666;
                        }
                        
                        .chat-main {
                            flex: 1;
                            display: flex;
                            flex-direction: column;
                        }
                        
                        .chat-header {
                            padding: 15px;
                            border-bottom: 1px solid var(--border-color);
                            background-color: #f9f9f9;
                            display: flex;
                            align-items: center;
                        }
                        
                        .chat-header .user-avatar {
                            width: 32px;
                            height: 32px;
                            font-size: 14px;
                        }
                        
                        .chat-header .user-name {
                            font-size: 16px;
                        }
                        
                        .chat-messages {
                            flex: 1;
                            padding: 15px;
                            overflow-y: auto;
                            background-color: #f9f9f9;
                        }
                        
                        .message {
                            margin-bottom: 15px;
                        }
                        
                        .message.received .message-content {
                            background-color: #e3f2fd;
                            border-radius: 0 18px 18px 18px;
                            margin-left: 15px;
                            padding: 10px 15px;
                            display: inline-block;
                            max-width: 70%;
                        }
                        
                        .message.sent {
                            text-align: right;
                        }
                        
                        .message.sent .message-content {
                            background-color: #e1f5fe;
                            border-radius: 18px 0 18px 18px;
                            margin-right: 15px;
                            padding: 10px 15px;
                            display: inline-block;
                            max-width: 70%;
                            text-align: left;
                        }
                        
                        .message-time {
                            font-size: 11px;
                            color: #666;
                            margin-top: 5px;
                        }
                        
                        .chat-input {
                            padding: 15px;
                            border-top: 1px solid var(--border-color);
                            background-color: #f9f9f9;
                        }
                        
                        .input-container {
                            display: flex;
                        }
                        
                        .message-input {
                            flex: 1;
                            padding: 10px 15px;
                            border: 1px solid #ddd;
                            border-radius: 20px;
                            font-size: 14px;
                            resize: none;
                            margin-right: 10px;
                        }
                        
                        .send-button {
                            background-color: var(--primary-color);
                            color: white;
                            border: none;
                            border-radius: 20px;
                            padding: 0 20px;
                            cursor: pointer;
                            font-weight: 500;
                        }
                        
                        .send-button:hover {
                            background-color: #3a85d8;
                        }
                        
                        @media (max-width: 768px) {
                            .chat-container {
                                flex-direction: column;
                                height: auto;
                            }
                            
                            .chat-sidebar {
                                width: 100%;
                                height: 300px;
                            }
                            
                            .chat-main {
                                height: 500px;
                            }
                            
                            .nav-list {
                                flex-direction: column;
                            }
                            
                            .nav-item {
                                margin-right: 0;
                                margin-bottom: 5px;
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <nav class="nav">
                            <ul class="nav-list">
                                <li class="nav-item"><a class="nav-link" href="/" hx-get="/" hx-target="body" hx-swap="innerHTML">Home</a></li>
                                <li class="nav-item"><a class="nav-link" href="/dashboard" hx-get="/dashboard" hx-target="body" hx-swap="innerHTML">Dashboard</a></li>
                                <li class="nav-item"><a class="nav-link" href="/theme" hx-get="/theme" hx-target="body" hx-swap="innerHTML">Theme</a></li>
                                <li class="nav-item"><a class="nav-link" href="/chat" hx-get="/chat" hx-target="body" hx-swap="innerHTML">Chat</a></li>
                            </ul>
                        </nav>
                        
                        <div class="chat-container">
                            <div class="chat-sidebar">
                                <div class="sidebar-header">
                                    <h2>Contacts</h2>
                                </div>
                                
                                <div class="user-search">
                                    <input type="text" class="search-input" placeholder="Search contacts...">
                                </div>
                                
                                <ul class="user-list">
                                    <li class="user-item active">
                                        <div class="user-info">
                                            <div class="user-avatar" style="background-color: #2196F3;">JD</div>
                                            <div class="user-details">
                                                <div class="user-name">John Doe</div>
                                                <div class="user-status">Online</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li class="user-item">
                                        <div class="user-info">
                                            <div class="user-avatar" style="background-color: #4CAF50;">AS</div>
                                            <div class="user-details">
                                                <div class="user-name">Alice Smith</div>
                                                <div class="user-status">Last seen 5m ago</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li class="user-item">
                                        <div class="user-info">
                                            <div class="user-avatar" style="background-color: #FF9800;">RJ</div>
                                            <div class="user-details">
                                                <div class="user-name">Robert Johnson</div>
                                                <div class="user-status">Last seen 1h ago</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li class="user-item">
                                        <div class="user-info">
                                            <div class="user-avatar" style="background-color: #9C27B0;">EB</div>
                                            <div class="user-details">
                                                <div class="user-name">Emily Brown</div>
                                                <div class="user-status">Online</div>
                                            </div>
                                        </div>
                                    </li>
                                    <li class="user-item">
                                        <div class="user-info">
                                            <div class="user-avatar" style="background-color: #F44336;">MW</div>
                                            <div class="user-details">
                                                <div class="user-name">Michael Wilson</div>
                                                <div class="user-status">Last seen yesterday</div>
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                            
                            <div class="chat-main">
                                <div class="chat-header">
                                    <div class="user-avatar" style="background-color: #2196F3;">JD</div>
                                    <div class="user-details">
                                        <div class="user-name">John Doe</div>
                                        <div class="user-status">Online</div>
                                    </div>
                                </div>
                                
                                <div class="chat-messages">
                                    <div class="message received">
                                        <div class="message-content">
                                            <p>Hi there! How are you doing today?</p>
                                            <div class="message-time">10:30 AM</div>
                                        </div>
                                    </div>
                                    
                                    <div class="message sent">
                                        <div class="message-content">
                                            <p>I'm doing great, thanks for asking! Just working on the Summon project. How about you?</p>
                                            <div class="message-time">10:32 AM</div>
                                        </div>
                                    </div>
                                    
                                    <div class="message received">
                                        <div class="message-content">
                                            <p>That sounds interesting! What kind of project is it?</p>
                                            <div class="message-time">10:33 AM</div>
                                        </div>
                                    </div>
                                    
                                    <div class="message sent">
                                        <div class="message-content">
                                            <p>It's a Kotlin multiplatform framework for building UIs with server-side rendering. Working on the Quarkus integration now.</p>
                                            <div class="message-time">10:36 AM</div>
                                        </div>
                                    </div>
                                    
                                    <div class="message received">
                                        <div class="message-content">
                                            <p>That sounds impressive! Would love to see a demo sometime.</p>
                                            <div class="message-time">10:37 AM</div>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="chat-input">
                                    <div class="input-container">
                                        <input type="text" class="message-input" placeholder="Type a message...">
                                        <button class="send-button">Send</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
                </body>
            </html>
        """.trimIndent()
    }
}

/**
 * Resource to help with diagnostics and testing static HTML pages
 */
@Path("/api")
class ApiResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(): String {
        System.out.println("**************** API HELLO ENDPOINT CALLED ****************")
        return "Hello from Summon Quarkus integration"
    }

    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    fun getDashboard(): Response {
        System.out.println("**************** API DASHBOARD REDIRECT CALLED ****************")
        return Response.seeOther(URI.create("/dashboard")).build()
    }

    @GET
    @Path("/chat")
    @Produces(MediaType.TEXT_HTML)
    fun getChat(): Response {
        System.out.println("**************** API CHAT REDIRECT CALLED ****************")
        return Response.seeOther(URI.create("/chat")).build()
    }

    @GET
    @Path("/theme")
    @Produces(MediaType.TEXT_HTML)
    fun getTheme(): Response {
        System.out.println("**************** API THEME REDIRECT CALLED ****************")
        return Response.seeOther(URI.create("/theme")).build()
    }

    @GET
    @Path("/dashboard-direct")
    @Produces(MediaType.TEXT_HTML)
    fun getDirectDashboard(): String {
        System.out.println("**************** API DASHBOARD-DIRECT CALLED ****************")
        return """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Direct Dashboard</title>
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
                          background-color: #f5f7f9;
                          margin: 0;
                          padding: 20px;
                        }
                        
                        .container {
                          max-width: 1200px;
                          margin: 0 auto;
                          padding: 20px;
                        }
                        
                        h1 {
                          color: #333;
                          margin-bottom: 20px;
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
                        
                        <h1>Direct Dashboard</h1>
                        
                        <div class="card">
                            <h2>Status</h2>
                            <p>This dashboard is served directly from the ApiResource class.</p>
                            <p>If you're seeing this page, the direct HTML rendering is working properly.</p>
                        </div>
                        
                        <div class="card">
                            <h2>Navigation Test</h2>
                            <p>Try these links:</p>
                            <ul>
                                <li><a href="/">Home</a></li>
                                <li><a href="/dashboard">Dashboard</a></li>
                                <li><a href="/theme">Theme</a></li>
                                <li><a href="/chat">Chat</a></li>
                                <li><a href="/direct-test">Summon test page</a></li>
                            </ul>
                        </div>
                    </div>
                    
                    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
                </body>
            </html>
        """.trimIndent()
    }

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_HTML)
    fun getApiTest(): String {
        System.out.println("**************** API TEST PAGE CALLED ****************")
        return """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>API Test</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            padding: 20px;
                            max-width: 800px;
                            margin: 0 auto;
                            background-color: #f0f0f0;
                        }
                        h1 { color: #333; }
                        .box {
                            background-color: white;
                            border: 1px solid #ddd;
                            padding: 20px;
                            border-radius: 5px;
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <h1>API Resource Test Page</h1>
                    <div class="box">
                        <p>This is a simple test page served directly from the ApiResource.</p>
                        <p>If you can see this, HTML rendering from the API resource is working correctly.</p>
                        <p>Try these links:</p>
                        <ul>
                            <li><a href="/">Home</a></li>
                            <li><a href="/dashboard">Dashboard</a></li>
                            <li><a href="/theme">Theme</a></li>
                            <li><a href="/chat">Chat</a></li>
                            <li><a href="/direct-test">Summon test page</a></li>
                        </ul>
                    </div>
                </body>
            </html>
        """.trimIndent()
    }
} 