package com.developerkim.mytodo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}

/*
        databaseHandler= DatabaseHandler(this,null,null,1)
        notelist = databaseHandler.showNotes(this)
        adapter = NoteAdapter(this,notelist!!)
        val recyclerView:RecyclerView = findViewById(R.id.rv)
        recyclerView.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        recyclerView.adapter = adapter*/
    /*private fun enableSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT ){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        p.color = Color.parseColor("#388E3C")
                        val background =
                            RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.delete_note)
                        val icon_dest = RectF(
                            itemView.left.toFloat() + width,
                            itemView.top.toFloat() + width,
                            itemView.left.toFloat() + 2 * width,
                            itemView.bottom.toFloat() - width
                        )
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else {
                        p.color = Color.parseColor("#D32F2F")
                        val background = RectF(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                        )
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.delete_note)
                        val icon_dest = RectF(
                            itemView.right.toFloat() - 2 * width,
                            itemView.top.toFloat() + width,
                            itemView.right.toFloat() - width,
                            itemView.bottom.toFloat() - width
                        )
                        c.drawBitmap(icon, null, icon_dest, p)
                    }
                }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT){
                    val deleteModel = notelist!![position]
                    adapter!!.removeItem(position)
//                    showing snackbar with undo
                    val snackbar = Snackbar.make(window.decorView.rootView, "removed from database",Snackbar.LENGTH_LONG)

                    snackbar.setAction("DELETED"){
//                        adapter!!.restoreItem(deleteModel,position)
                    }
                    snackbar.setActionTextColor(Color.RED)
                    snackbar.show()
                } else{
                    val deletedModel = notelist!![position]
                    adapter!!.removeItem(position)
                    // showing snack bar with Undo option
                    val snackbar = Snackbar.make(
                        window.decorView.rootView,
                        " removed from Recyclerview!",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.setAction("UNDO") {
                        // undo is selected, restore the deleted item
//                        adapter!!.restoreItem(deletedModel, position)
                    }
                    snackbar.setActionTextColor(Color.YELLOW)
                    snackbar.show()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rv)

    }*/

   /* private fun viewNotes() {
        databaseHandler= DatabaseHandler(this,null,null,1)
        notelist = databaseHandler.showNotes(this)
        adapter = NoteAdapter(this,notelist!!)
        val recyclerView:RecyclerView = findViewById(R.id.rv)
        recyclerView.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        recyclerView.adapter = adapter


    }*/




