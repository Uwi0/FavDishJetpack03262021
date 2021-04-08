package com.kakapo.favoritdish.model.database

import androidx.room.Dao
import androidx.room.Insert
import com.kakapo.favoritdish.model.entities.FavDish

@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)



}