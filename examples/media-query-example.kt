package examples

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.style.GlobalStyle
import codes.yousef.summon.components.style.MediaQuery
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.modifier.Modifier

/**
 * Example demonstrating how to use MediaQuery to generate actual CSS @media rules
 * in the HTML output (not just data attributes).
 */

@Composable
fun ResponsiveNavigationExample() {
    // This generates an actual <style> block in the <head> with @media queries
    MediaQuery(
        query = "@media (max-width: 768px)",
        css = """
            #app-header-nav {
                display: none !important;
            }
            #hamburger-btn {
                display: block !important;
            }
        """
    )
    
    // You can also add more media queries for different breakpoints
    MediaQuery(
        query = "@media (min-width: 769px)",
        css = """
            #app-header-nav {
                display: flex !important;
            }
            #hamburger-btn {
                display: none !important;
            }
        """
    )
    
    // Or combine multiple rules in a single GlobalStyle call
    GlobalStyle(css = """
        @media (max-width: 768px) {
            #app-header-nav { display: none !important; }
            #hamburger-btn { display: block !important; }
        }
        
        @media (min-width: 769px) {
            #app-header-nav { display: flex !important; }
            #hamburger-btn { display: none !important; }
        }
    """)
    
    // The actual navigation elements
    Box(modifier = Modifier().id("app-header-nav")) {
        // Navigation content
    }
    
    Box(modifier = Modifier().id("hamburger-btn")) {
        // Hamburger menu button
    }
}

/**
 * Alternative approach using GlobalStyle for more complex CSS
 */
@Composable
fun ComplexResponsiveStyles() {
    GlobalStyle(css = """
        /* Mobile styles */
        .container {
            padding: 1rem;
        }
        
        .nav-menu {
            flex-direction: column;
        }
        
        /* Tablet and up */
        @media (min-width: 768px) {
            .container {
                padding: 2rem;
                max-width: 768px;
                margin: 0 auto;
            }
            
            .nav-menu {
                flex-direction: row;
                gap: 2rem;
            }
        }
        
        /* Desktop and up */
        @media (min-width: 1024px) {
            .container {
                max-width: 1024px;
            }
            
            .hero-title {
                font-size: 3rem;
            }
        }
        
        /* Dark mode support */
        @media (prefers-color-scheme: dark) {
            body {
                background-color: #1a1a1a;
                color: #ffffff;
            }
            
            .card {
                background-color: #2d2d2d;
                border-color: #404040;
            }
        }
    """)
}

/**
 * How the output looks in HTML:
 * 
 * <head>
 *   <style>
 *     @media (max-width: 768px) {
 *       #app-header-nav {
 *         display: none !important;
 *       }
 *       #hamburger-btn {
 *         display: block !important;
 *       }
 *     }
 *   </style>
 * </head>
 * 
 * <body>
 *   <div id="app-header-nav">...</div>
 *   <div id="hamburger-btn">...</div>
 * </body>
 */
