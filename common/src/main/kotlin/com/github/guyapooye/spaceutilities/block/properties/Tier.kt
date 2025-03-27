package com.github.guyapooye.spaceutilities.block.properties

import net.minecraft.util.StringRepresentable

enum class Tier(name: String, val multiplier: Int) : StringRepresentable{
    BASIC("Basic", 1), INTERMEDIATE("Intermediate", 2), ADVANCED("Advanced", 4), BEST("Best", 8);

    override fun getSerializedName() = name
}