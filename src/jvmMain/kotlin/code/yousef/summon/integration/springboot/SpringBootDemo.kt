package code.yousef.summon.integration.springboot

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.runtime.Composable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import jakarta.servlet.http.HttpServletResponse

/**
 * Demo of Summon integration with Spring Boot.
 * This file shows example usage of the various integration components.
 */
object SpringBootDemo {

    /**
     * Example of a Summon component that will be used in a Spring application
     */
    @Composable
    fun GreetingComponent(name: String = "World") {
        Column {
            Text(text = "Hello, $name!")
            Button(
                label = "Click me",
                onClick = { /* handle click */ }
            )
        }
    }

    /**
     * Auto-configuration class for Summon integration with Spring Boot
     */
    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnClass(name = ["org.springframework.web.servlet.config.annotation.WebMvcConfigurer"])
    class SummonAutoConfiguration {
        init {
            // Register components with the Thymeleaf integration
            SummonExpressionObject.registerGlobalComponent("Greeting") { props ->
                {
                    val name = props["name"] as? String ?: "World"
                    GreetingComponent(name)
                }
            }
        }
    }

    /**
     * Example of a Spring MVC controller using Summon components
     */
    @Controller
    class SummonDemoController {
        
        @Autowired
        private lateinit var thymeleafDialect: SummonThymeleafDialect
        
        /**
         * Example using direct rendering with ResponseEntity
         */
        @GetMapping("/summon/direct", produces = [MediaType.TEXT_HTML_VALUE])
        @ResponseBody
        fun directRendering(): ResponseEntity<String> {
            val renderer = SpringBootRenderer()
            return renderer.render(title = "Direct Summon Rendering") {
                GreetingComponent("Spring User")
            }
        }
        
        /**
         * Example using HttpServletResponse
         */
        @GetMapping("/summon/response")
        fun responseRendering(response: HttpServletResponse) {
            val renderer = SpringBootRenderer()
            renderer.renderToResponse(response, "Response Rendering") {
                GreetingComponent("Response User")
            }
        }
        
        /**
         * Example using Thymeleaf template with Summon components
         */
        @GetMapping("/summon/thymeleaf")
        fun thymeleafRendering(model: Model): String {
            // Add data to the model that will be available in the template
            model.addAttribute("pageTitle", "Summon with Thymeleaf")
            model.addAttribute("username", "Thymeleaf User")
            
            // Return the template name
            return "summon-example"
        }
    }
} 