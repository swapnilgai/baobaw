package com.java.baobaw.di

import com.java.baobaw.BuildKonfig
import com.java.baobaw.model.DevelopmentEnvironment
import com.java.baobaw.model.ProductionEnvironment
import com.java.baobaw.model.ProjectEnvironment
import org.koin.dsl.module

val appModule = module {
    single<ProjectEnvironment> {
       if(BuildKonfig.environment == "dev")
          DevelopmentEnvironment()
       else
          ProductionEnvironment()
    }
}


