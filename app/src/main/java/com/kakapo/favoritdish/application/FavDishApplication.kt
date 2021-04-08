package com.kakapo.favoritdish.application

import android.app.Application
import com.kakapo.favoritdish.model.database.FavDishRepository
import com.kakapo.favoritdish.model.database.FavDishRoomDatabase

class FavDishApplication: Application() {
    private val database by lazy {
        FavDishRoomDatabase.getDatabase((this))
    }

    val repository by lazy{
        FavDishRepository(database.favDishDao())
    }
}