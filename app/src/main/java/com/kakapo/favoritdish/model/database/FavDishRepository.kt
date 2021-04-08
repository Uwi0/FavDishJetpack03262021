package com.kakapo.favoritdish.model.database

import com.kakapo.favoritdish.model.entities.FavDish

class FavDishRepository(private val favDishDao: FavDishDao) {

    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }
}