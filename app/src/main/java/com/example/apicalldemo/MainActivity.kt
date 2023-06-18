package com.example.apicalldemo

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var textViewPrintData : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewPrintData = findViewById(R.id.jsonData)

        callAPILoginAsyncTask().execute()
    }
    private inner class callAPILoginAsyncTask() : AsyncTask<Any, Void, String>() {

        private lateinit var customProgressDialog : Dialog
        override fun onPreExecute() {
            super.onPreExecute()
            showDialog()
        }
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Any?): String {
            var result  = ""

            var connection : HttpURLConnection? = null
            try {
                val url = URL("https://run.mocky.io/v3/3c309e21-d1ea-41be-9cb7-9f0ea7bef646")
                connection = url.openConnection() as HttpURLConnection

                connection.doInput = true
                connection.doOutput = true

                // receive data
                val httpResult : Int = connection.responseCode
                if(httpResult == HttpURLConnection.HTTP_OK){

                    //read data from website
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val stringBuilder = java.lang.StringBuilder()

                    var line : String?
                    try {
                        while(reader.readLine().also {line = it} != null)
                        {
                            stringBuilder.append(line +"\n")
                        }
                    }catch (e : IOException){
                        e.printStackTrace()
                    }
                    finally {
                        try {
                            inputStream.close()
                        }catch (e : IOException){
                            e.printStackTrace()
                        }
                    }
                    result = String(stringBuilder = stringBuilder)
                }
                else{
                    result = connection.responseMessage
                }

            }catch (e : SocketTimeoutException){
                result = "Connection TImeout"
            }catch (e : java.lang.Exception){
                result = "Error "+ e.message
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.i("JSON response result: ",result!!)
            textViewPrintData.text = result
            cancelDialog()
        }

        private fun cancelDialog(){
            customProgressDialog.dismiss()
        }
        private fun showDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.custom_progress_dialog);
            customProgressDialog.show()

        }

    }
}