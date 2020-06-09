package es.situm.gettingstarted.fetchresources

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import es.situm.gettingstarted.R
import es.situm.gettingstarted.common.SampleActivity
import es.situm.sdk.SitumSdk
import es.situm.sdk.communication.CommunicationConfig
import es.situm.sdk.communication.CommunicationConfigImpl
import es.situm.sdk.configuration.network.NetworkOptions
import es.situm.sdk.configuration.network.NetworkOptionsImpl
import es.situm.sdk.error.Error
import es.situm.sdk.model.cartography.*
import es.situm.sdk.utils.Handler
import kotlinx.android.synthetic.main.activity_fetch_resources.*

class FetchResourcesActivity : SampleActivity() {

    private val cacheStrategies = NetworkOptions.CacheStrategy.values().also { it.sortBy { item -> item.toString() } }
    var selectedCacheStrategy: NetworkOptions.CacheStrategy = NetworkOptions.CacheStrategy.TIMED_CACHE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetch_resources)

        initCacheStrategies()
    }

    private fun createConfigurationObject(): CommunicationConfig {
        return CommunicationConfigImpl(
                NetworkOptionsImpl.Builder()
                        .setCacheStrategy(selectedCacheStrategy)
                        .setPreloadImages(cbPreloadImages.isChecked)
                        .build()
        )
    }

    private fun initCacheStrategies() {
        ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                cacheStrategies
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spCacheStrategies.adapter = adapter

            spCacheStrategies.setSelection(cacheStrategies.indexOf(NetworkOptions.CacheStrategy.TIMED_CACHE))
        }

        spCacheStrategies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCacheStrategy = cacheStrategies[position]
            }
        }
    }

    fun fetchBuidingInfo(view: View) {

        SitumSdk.communicationManager().fetchBuildingInfo(buildingFromIntent.identifier, createConfigurationObject(), object : Handler<BuildingInfo> {
            override fun onSuccess(obtained: BuildingInfo?) {
                notifySuccess("fetchBuildingInfo", 1)
            }

            override fun onFailure(error: Error?) {
                notifyError("fetchBuildingInfo", error?.message)
            }
        })
    }

    fun fetchIndoorPOIsFromBuilding(view: View) {
        SitumSdk.communicationManager().fetchIndoorPOIsFromBuilding(buildingFromIntent.identifier, createConfigurationObject(), object : Handler<Collection<Poi>> {

            override fun onSuccess(obtained: Collection<Poi>?) {
                notifySuccess("fetchIndoorPOIsFromBuilding", obtained?.count() ?: 0)
            }

            override fun onFailure(error: Error?) {
                notifyError("fetchIndoorPOIsFromBuilding", error?.message)
            }
        })
    }

    fun fetchOutdoorPOIsFromBuilding(view: View) {
        SitumSdk.communicationManager().fetchOutdoorPOIsFromBuilding(buildingFromIntent.identifier, createConfigurationObject(), object : Handler<Collection<Poi>> {

            override fun onSuccess(obtained: Collection<Poi>?) {
                notifySuccess("fetchOutdoorPOIsFromBuilding", obtained?.count() ?: 0)
            }

            override fun onFailure(error: Error?) {
                notifyError("fetchOutdoorPOIsFromBuilding", error?.message)
            }
        })
    }

    fun fetchFloorsFromBuilding(view: View) {
        SitumSdk.communicationManager().fetchFloorsFromBuilding(buildingFromIntent.identifier, createConfigurationObject(), object : Handler<Collection<Floor>> {
            override fun onSuccess(obtained: Collection<Floor>?) {
                notifySuccess("fetchFloorsFromBuilding", obtained?.count() ?: 0)
            }

            override fun onFailure(error: Error?) {
                notifyError("fetchFloorsFromBuilding", error?.message)
            }
        })
    }

    fun fetchPoiCategories(view: View) {
        SitumSdk.communicationManager().fetchPoiCategories(createConfigurationObject(), object : Handler<Collection<PoiCategory>> {
            override fun onSuccess(obtained: Collection<PoiCategory>?) {
                notifySuccess("fetchPoiCategories", obtained?.count() ?: 0)
            }

            override fun onFailure(error: Error?) {
                notifyError("fetchPoiCategories", error?.message)
            }
        })
    }

    fun fetchGeofencesFromBuilding(view: View) {
        SitumSdk.communicationManager().fetchGeofencesFromBuilding(buildingFromIntent, createConfigurationObject(), object : Handler<List<Geofence>> {
            override fun onSuccess(obtained: List<Geofence>?) {
                notifySuccess("fetchGeofencesFromBuilding", obtained?.count() ?: 0)
            }

            override fun onFailure(error: Error?) {
                notifyError("fetchGeofencesFromBuilding", error?.message)
            }
        })
    }

    fun notifyError(callName: String, errorMessage: String?) {
        Snackbar.make(clCommContainer, "$callName error: $errorMessage", Snackbar.LENGTH_LONG).show()
    }

    fun notifySuccess(callName: String, returnCount: Int) {
        Snackbar.make(clCommContainer, resources.getQuantityString(R.plurals.success_result, returnCount, callName, returnCount), Snackbar.LENGTH_LONG).show()
    }
}
