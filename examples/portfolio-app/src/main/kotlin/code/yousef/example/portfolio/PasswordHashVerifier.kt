package code.yousef.example.portfolio

import org.mindrot.jbcrypt.BCrypt

fun main() {
    val storedHash = "\$2a\$10\$8jw.9MmH.xZkJXFXJlzJs.jYnMAhOyHRhHlSW1JjQGD5XL9BdHYBu"
    
    println("Testing BCrypt hash: $storedHash")
    println("=" * 60)
    
    // Test if the hash matches 'admin123'
    val matchesAdmin123 = BCrypt.checkpw("admin123", storedHash)
    println("Hash matches 'admin123': $matchesAdmin123")
    
    // Test some other common passwords
    val passwords = listOf(
        "admin",
        "password",
        "admin123",
        "123456", 
        "password123",
        "Admin123",
        "admin@123",
        "test123",
        "admin1234",
        "summon"
    )
    
    println("\nTesting common passwords against the hash:")
    println("-" * 40)
    for (password in passwords) {
        try {
            val matches = BCrypt.checkpw(password, storedHash)
            if (matches) {
                println("✓ '$password': MATCHES!")
            } else {
                println("✗ '$password': does not match")
            }
        } catch (e: Exception) {
            println("! '$password': error - ${e.message}")
        }
    }
    
    println("\n" + "=" * 60)
    println("Generating a correct hash for 'admin123':")
    val correctHash = BCrypt.hashpw("admin123", BCrypt.gensalt(10))
    println("New hash: $correctHash")
    
    // Verify the new hash
    val verifyNewHash = BCrypt.checkpw("admin123", correctHash)
    println("New hash verification: $verifyNewHash")
}

private operator fun String.times(n: Int): String = repeat(n)