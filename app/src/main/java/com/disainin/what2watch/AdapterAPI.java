package com.disainin.what2watch;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class AdapterAPI extends ArrayAdapter {

    //URL IMAGENES --> https://image.tmdb.org/t/p/w300_and_h450_bestv2/

    private final int SIMPLE_REQUEST = 1;
    private static final String URL_BASE = "https://api.themoviedb.org/3/search/multi";
    private static final String URL_JSON = "?api_key=1947a2516ec6cb3cf97ef1da21fdaa87&language=" + Locale.getDefault().getLanguage().toLowerCase();
    private TextView resultado;

    public AdapterAPI(Context context, int resource, String query, TextView... tv) {
        super(context, resource);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        resultado = tv[0];
        resultado.setText("");

        JsonObjectRequest request = new JsonObjectRequest(URL_BASE + URL_JSON + "&query=" + query.replaceAll(" ", "%20"), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                resultado.setText(response.toString());

                if (response != null) {
                    int resultCount = response.optInt("total_results");
                    if (resultCount > 0) {
                        Gson gson = new Gson();
                        JSONArray jsonArray = response.optJSONArray("results");
                        Item[] items = gson.fromJson(jsonArray.toString(), Item[].class);

                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Item item = items[i];
                                if (item.getMedia_type().equals("person")) {
                                    Persona persona = gson.fromJson(jsonArray.optString(i), Persona.class);
                                    resultado.append("(Persona) " + persona.getName() + "\n");

                                    //ULTRA HIPER MEGA REFACTORIZAR TO DO ESTO
                                    //AÑADIR QUE CUANDO SE HAGA LA MISMA BUSQUEDA SEGUIDA NO VUELVA A LLAMAR A LA API (ESTO HACERLO EN LOS EVENTOS DEL UI)

                                    for (int j = 0; j < persona.getKnown_for().length; j++) {
                                        JsonObject relacionado = persona.getKnown_for()[j];
                                        Item conocidopor = gson.fromJson(relacionado.toString(), Item.class);

                                        if (conocidopor.getVote_count() > 0) {
                                            if ((relacionado.get("media_type") + "").equals("\"movie\"")) {
                                                Pelicula pelicula = gson.fromJson(relacionado.toString(), Pelicula.class);
                                                resultado.append(pelicula.getTitle() + " (" + pelicula.getRelease_date() + ") ");

                                            } else if ((relacionado.get("media_type") + "").equals("\"tv\"")) {
                                                Serie serie = gson.fromJson(relacionado.toString(), Serie.class);
                                                resultado.append(serie.getName() + " (" + serie.getFirst_air_date() + ") ");
                                            }

                                            resultado.append(conocidopor.getVote_average() + "\n\n");
                                        }
                                    }

                                    resultado.append("\n\n\n");

                                } else {
                                    if (item.getVote_count() > 0) {
                                        if (item.getMedia_type().equals("movie")) {
                                            Pelicula pelicula = gson.fromJson(jsonArray.optString(i), Pelicula.class);
                                            resultado.append(pelicula.getTitle() + " (" + pelicula.getRelease_date() + ") ");
                                        } else if (item.getMedia_type().equals("tv")) {
                                            Serie serie = gson.fromJson(jsonArray.optString(i), Serie.class);
                                            resultado.append(serie.getName() + " (" + serie.getFirst_air_date() + ") ");
                                        }

                                        resultado.append(item.getVote_average() + "\n");
                                    }
                                }

                            }
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Procesar VolleyError
            }
        });

        request.setTag(SIMPLE_REQUEST);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}
