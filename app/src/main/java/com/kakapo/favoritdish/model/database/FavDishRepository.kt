package com.kakapo.favoritdish.model.database

import com.kakapo.favoritdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }

    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishesList()
}