package com.youravgjoe.circlecropiconpack

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory


class IconUtils(context: Context) {

    private var mIcons: Map<String, Drawable>

    init {
        mIcons = getIcons(context, getPackageNames(context))
    }

    companion object {

        fun getPackageNames(context: Context): List<String> {
            val packageManager: PackageManager = context.packageManager
            val packages: List<ApplicationInfo> = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            val packageNames: MutableList<String> = mutableListOf()
            for (p in packages) {
                packageNames.add(p.packageName)
            }
            return packageNames
        }

        fun getIcons(context: Context, packageNames: List<String>): Map<String, Drawable> {
            val icons: MutableMap<String, Drawable> = mutableMapOf()
            for (packageName in packageNames) {
                val icon = getIcon(context, packageName)
                if (icon != null) {
                    icons[packageName] = icon
                }
            }
            return icons
        }

        private fun getIcon(context: Context, packageName: String): Drawable? {
            var icon: Drawable? = null
            try {
                icon = context.packageManager.getApplicationIcon(packageName) ?: return null
                icon = circleCrop(context, icon)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return icon
        }

        private fun circleCrop(context: Context, drawable: Drawable): Drawable {
            val roundIcon = RoundedBitmapDrawableFactory.create(context.resources, (drawable as BitmapDrawable).bitmap)
            roundIcon.isCircular = true
            return roundIcon
        }
    }
}