package code.yousef.example.quarkus

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger

/**
 * Minimal web resource that doesn't use Summon components
 */
@Path("/minimal")
@ApplicationScoped
class MinimalWebResource {
    private val logger = Logger.getLogger(MinimalWebResource::class.java)

    init {
        logger.info("MinimalWebResource initialized")
        println("MinimalWebResource initialized")
    }

    /**
     * Render a simple home page without Summon components
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        logger.info("MinimalWebResource.home() called")
        println("MinimalWebResource.home() called")
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Minimal Home Page</title>
                <style>
                    body { 
                        font-family: system-ui, sans-serif; 
                        line-height: 1.5;
                        padding: 2rem;
                        max-width: 800px;
                        margin: 0 auto;
                        color: #333;
                    }
                    h1 { color: #4695EB; }
                    .card {
                        border: 1px solid #ddd;
                        border-radius: 8px;
                        padding: 1rem;
                        margin: 1rem 0;
                        background-color: #f9f9f9;
                    }
                    nav {
                        background-color: #f0f0f0;
                        padding: 10px;
                        margin-bottom: 20px;
                        border-radius: 5px;
                    }
                    nav a {
                        margin-right: 15px;
                        color: #4695EB;
                        text-decoration: none;
                    }
                    nav a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <nav>
                    <a href="/minimal">Home</a>
                    <a href="/minimal/dashboard">Dashboard</a>
                    <a href="/minimal/users">Users</a>
                    <a href="/test">Test</a>
                    <a href="/test/html">Test HTML</a>
                </nav>
                <h1>Minimal Home Page</h1>
                <div class="card">
                    <p>This is a simple home page without using Summon rendering.</p>
                    <p>The Summon library is compiled with Kotlin 2.0.0 (metadata version 2.1.0), but Quarkus is using a Kotlin version that can only read up to version 2.0.0 metadata.</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    /**
     * Render a simple dashboard page without Summon components
     */
    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    fun dashboard(): String {
        logger.info("MinimalWebResource.dashboard() called")
        println("MinimalWebResource.dashboard() called")
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Minimal Dashboard</title>
                <style>
                    body { 
                        font-family: system-ui, sans-serif; 
                        line-height: 1.5;
                        padding: 2rem;
                        max-width: 800px;
                        margin: 0 auto;
                        color: #333;
                    }
                    h1 { color: #4695EB; }
                    .card {
                        border: 1px solid #ddd;
                        border-radius: 8px;
                        padding: 1rem;
                        margin: 1rem 0;
                        background-color: #f9f9f9;
                    }
                    nav {
                        background-color: #f0f0f0;
                        padding: 10px;
                        margin-bottom: 20px;
                        border-radius: 5px;
                    }
                    nav a {
                        margin-right: 15px;
                        color: #4695EB;
                        text-decoration: none;
                    }
                    nav a:hover {
                        text-decoration: underline;
                    }
                    .dashboard-widget {
                        background-color: #e3f2fd;
                        border-radius: 8px;
                        padding: 15px;
                        margin-bottom: 15px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                    .dashboard-stats {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                        gap: 15px;
                        margin-bottom: 20px;
                    }
                    .stat-box {
                        background-color: #fff;
                        border-radius: 5px;
                        padding: 15px;
                        text-align: center;
                        box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                    }
                    .stat-value {
                        font-size: 24px;
                        font-weight: bold;
                        color: #4695EB;
                    }
                    .stat-label {
                        font-size: 14px;
                        color: #666;
                    }
                </style>
            </head>
            <body>
                <nav>
                    <a href="/minimal">Home</a>
                    <a href="/minimal/dashboard">Dashboard</a>
                    <a href="/minimal/users">Users</a>
                    <a href="/test">Test</a>
                    <a href="/test/html">Test HTML</a>
                </nav>
                <h1>Minimal Dashboard</h1>
                <div class="dashboard-stats">
                    <div class="stat-box">
                        <div class="stat-value">42</div>
                        <div class="stat-label">Active Users</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-value">156</div>
                        <div class="stat-label">Total Visitors</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-value">89%</div>
                        <div class="stat-label">System Health</div>
                    </div>
                </div>
                <div class="dashboard-widget">
                    <h3>Recent Activity</h3>
                    <p>• User "john_doe" logged in 5 minutes ago</p>
                    <p>• New user "alice_smith" registered today</p>
                    <p>• System update scheduled for tomorrow</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    /**
     * Render a simple users page without Summon components
     */
    @GET
    @Path("/users")
    @Produces(MediaType.TEXT_HTML)
    fun users(): String {
        logger.info("MinimalWebResource.users() called")
        println("MinimalWebResource.users() called")
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Minimal Users Page</title>
                <style>
                    body { 
                        font-family: system-ui, sans-serif; 
                        line-height: 1.5;
                        padding: 2rem;
                        max-width: 800px;
                        margin: 0 auto;
                        color: #333;
                    }
                    h1 { color: #4695EB; }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin: 20px 0;
                    }
                    th, td {
                        padding: 10px;
                        text-align: left;
                        border-bottom: 1px solid #ddd;
                    }
                    th {
                        background-color: #f5f5f5;
                        font-weight: bold;
                    }
                    tr:hover {
                        background-color: #f9f9f9;
                    }
                    nav {
                        background-color: #f0f0f0;
                        padding: 10px;
                        margin-bottom: 20px;
                        border-radius: 5px;
                    }
                    nav a {
                        margin-right: 15px;
                        color: #4695EB;
                        text-decoration: none;
                    }
                    nav a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <nav>
                    <a href="/minimal">Home</a>
                    <a href="/minimal/dashboard">Dashboard</a>
                    <a href="/minimal/users">Users</a>
                    <a href="/test">Test</a>
                    <a href="/test/html">Test HTML</a>
                </nav>
                <h1>User Management</h1>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Role</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>1</td>
                            <td>John Doe</td>
                            <td>john.doe@example.com</td>
                            <td>Administrator</td>
                        </tr>
                        <tr>
                            <td>2</td>
                            <td>Jane Smith</td>
                            <td>jane.smith@example.com</td>
                            <td>Editor</td>
                        </tr>
                        <tr>
                            <td>3</td>
                            <td>Bob Johnson</td>
                            <td>bob.johnson@example.com</td>
                            <td>Viewer</td>
                        </tr>
                        <tr>
                            <td>4</td>
                            <td>Alice Brown</td>
                            <td>alice.brown@example.com</td>
                            <td>Editor</td>
                        </tr>
                        <tr>
                            <td>5</td>
                            <td>Charlie Wilson</td>
                            <td>charlie.wilson@example.com</td>
                            <td>Viewer</td>
                        </tr>
                    </tbody>
                </table>
            </body>
            </html>
        """.trimIndent()
    }
} 