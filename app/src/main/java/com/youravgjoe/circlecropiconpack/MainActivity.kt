package com.youravgjoe.circlecropiconpack

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.Intent.ShortcutIconResource
import android.content.res.Resources
import android.view.View
import android.widget.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.view.WindowManager
import android.widget.AdapterView


private const val ACTION_ADW_PICK_ICON = "org.adw.launcher.icons.ACTION_PICK_ICON"
private const val ACTION_ADW_PICK_RESOURCE = "org.adw.launcher.icons.ACTION_PICK_ICON_RESOURCE"
//private const val REQUEST_CODE_READ_STORAGE = 0
//private const val REQUEST_CODE_WRITE_STORAGE = 1

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var mPickerMode = false
    private var mResourceMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // todo: (probably doesn't require permissions...?)
        // check to see if they have the xml/drawable files written in internal storage
        // if they do,
            // read in the xml, and check if any apps need to be added or removed
        // add/remove/update the files
        // use the xml files to do all the icon stuff


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val result =PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//            if (result != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_STORAGE)
//            } else {
//
//            }
//        }



        val iconSize = resources.getDimensionPixelSize(android.R.dimen.app_icon_size)

        val gridView: GridView = findViewById(R.id.icon_grid)
        gridView.numColumns = GridView.AUTO_FIT
        gridView.columnWidth = iconSize
        gridView.stretchMode = GridView.STRETCH_SPACING_UNIFORM
        gridView.verticalSpacing = iconSize / 3
        gridView.onItemClickListener = this
        val adapter = IconsAdapter(this, iconSize)
        gridView.adapter = adapter
        if (intent.action == ACTION_ADW_PICK_ICON) {
            mPickerMode = true
        }
        if (intent.hasExtra(ACTION_ADW_PICK_RESOURCE)) {
            mResourceMode = true
        }
    }

    @Suppress("DEPRECATION")
    override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        if (mPickerMode) {
            val intent = Intent()
            if (!mResourceMode) {
                var bitmap: Bitmap? = null
                try {
                    bitmap = adapterView.adapter.getItem(position) as Bitmap
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (bitmap != null) {
                    intent.putExtra("icon", bitmap)
                    setResult(Activity.RESULT_OK, intent)
                } else {
                    setResult(Activity.RESULT_CANCELED, intent)
                }
            } else {
                val res = (adapterView.adapter as IconsAdapter).getResource(position)
                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, res)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
    }

    private inner class IconsAdapter(private val mContext: Context, private val mIconSize: Int) : BaseAdapter() {

        private var mThumbs: ArrayList<Int>? = null

        init {
            loadIcons()
        }

        override fun getCount(): Int {
            return mThumbs!!.size
        }

        fun getResource(position: Int): ShortcutIconResource {
            return ShortcutIconResource.fromContext(mContext, mThumbs!![position])
        }

        override fun getItem(position: Int): Any {
            val opts = BitmapFactory.Options()
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888
            return BitmapFactory.decodeResource(mContext.resources, mThumbs!![position], opts)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val imageView: ImageView
            if (convertView == null) {
                imageView = ImageView(mContext)
                imageView.layoutParams = WindowManager.LayoutParams(mIconSize, mIconSize)
            } else {
                imageView = convertView as ImageView
            }
            imageView.setImageResource(mThumbs!![position])
            return imageView
        }

        private fun loadIcons() {
            mThumbs = ArrayList()
            val resources = resources
            val packageName = application.packageName
            addIcons(resources, packageName)
        }

        private fun addIcons(resources: Resources, packageName: String) {
            val extras = IconUtils.getPackageNames(this@MainActivity.baseContext)
            for (extra in extras) {
                val res = resources.getIdentifier(extra, "drawable", packageName)
                if (res != 0) {
                    val thumbRes = resources.getIdentifier(extra, "drawable", packageName)
                    if (thumbRes != 0) {
                        mThumbs!!.add(thumbRes)
                    }
                }
            }
        }

    }
}
