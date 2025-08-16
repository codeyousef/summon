package code.yousef.example.portfolio.plugins

import code.yousef.example.portfolio.auth.AuthService
import code.yousef.example.portfolio.models.*
import code.yousef.example.portfolio.repository.*
import code.yousef.summon.runtime.PlatformRenderer
import org.koin.dsl.module

val appModule = module {
    single { PlatformRenderer() }
    single { AuthService() }
}

val databaseModule = module {
    // Database configuration is handled in Database.kt
}

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<PortfolioRepository> { PortfolioRepositoryExposedImpl() }
}