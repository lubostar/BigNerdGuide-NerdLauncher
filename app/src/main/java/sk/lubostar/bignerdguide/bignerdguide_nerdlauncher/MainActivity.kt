package sk.lubostar.bignerdguide.bignerdguide_nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_nerd_launcher.*

class MainActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)
        app_recycler_view.layoutManager = LinearLayoutManager(this)
        setupAdapter()
    }

    private fun setupAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = packageManager.queryIntentActivities(startupIntent, 0)
        activities.sortWith(Comparator { a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString())

        })

        Log.i(TAG, "Found ${activities.size} activities");
        app_recycler_view.adapter = ActivityAdapter(activities)
    }

    private class ActivityAdapter(val activities: List<ResolveInfo>) :
        RecyclerView.Adapter<ActivityHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(android.R.layout.activity_list_item,
                parent, false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount() = activities.size
    }

    private class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private lateinit var resolveInfo: ResolveInfo
        private var appName: TextView = itemView.findViewById(android.R.id.text1)
        private var appIcon: ImageView = itemView.findViewById(android.R.id.icon)

        init {
            itemView.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager

            itemView.setPadding(40, 30, 0, 60)
            appName.text = resolveInfo.loadLabel(packageManager).toString()
            appIcon.setImageDrawable(resolveInfo.loadIcon(packageManager))
        }

        override fun onClick(view: View?) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            view?.context?.startActivity(intent)
        }
    }
}