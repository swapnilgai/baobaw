package com.java.cherrypick.di

import com.java.cherrypick.BuildKonfig
import com.java.cherrypick.model.DevelopmentEnvironment
import com.java.cherrypick.model.ProductionEnvironment
import com.java.cherrypick.model.ProjectEnvironment
import org.koin.dsl.module

val appModule = module {
    single<ProjectEnvironment> {
       if(BuildKonfig.environment == "dev")
          DevelopmentEnvironment()
       else
          ProductionEnvironment()
    }
}


