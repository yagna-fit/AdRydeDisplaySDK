package com.adryde.mobile.displaysdk.data.user.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

/**
 * Model for password auth response
 *
 * @property name
 * @property token_type
 * @property email
 * @property token
 * @property decryption_key
 */
@JsonClass(generateAdapter = true)
data class SyncDataResponseWire(
    val success: Boolean,
    val data: SyncDataModel,
    val message: String,
) {
    internal fun mapToSyncDataResponse(): SyncDataResponse {
        return SyncDataResponse(data.vessel_data, data.master_data, image_base_path = data.image_base_path )
    }
}

@JsonClass(generateAdapter = true)
data class SyncDataModel(
    val vessel_data: List<VesselDataModel> = arrayListOf(),
    val master_data: List<MasterDataModel> = arrayListOf(),
    val image_base_path : String = ""
)

@JsonClass(generateAdapter = true)
data class VesselDataModel(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("registration_number")
    var registrationNumber: String? = null,

    @SerializedName("vessel_name")
    var vesselName: String? = null,

    @SerializedName("vessel_description")
    var vesselDescription: String? = null,

    @SerializedName("capacity")
    var capacity: Int? = null,

    @SerializedName("class")
    var className: String? = null,

    @SerializedName("color_scheme")
    var colorScheme: String? = null,

    @SerializedName("owner_name")
    var ownersName: String? = null,

    @SerializedName("owner_address")
    var ownersAddress: String? = null,

    @SerializedName("date_issued")
    var dateIssued: String? = null,

    @SerializedName("date_expire")
    var dateExpire: String? = null,

    @SerializedName("created_at")
    var createdAt: String? = null,

    @SerializedName("last_modified_at")
    var lastModifiedAt: String? = null,

    @SerializedName("document")
    var document: ArrayList<String> = arrayListOf()
) {
   /* internal  fun getVesselDataTable(): VesselTableData {
        return  VesselTableData(
         id = id,
         registration_no = registrationNumber,
         vessel_name	= vesselName,
         vessel_description	= if(vesselDescription==null|| vesselDescription!!.isEmpty() || vesselDescription!!.toLowerCase()=="null")"-" else vesselDescription,
         tank_capacity = capacity,
         class_name	= className,
         color_of_vessel	= colorScheme,
         owner_name	= ownersName,
         owner_address	= ownersAddress,
         issue_date	=  dateIssued,
         expire_date	= dateExpire,
         created_at	= createdAt,
         updated_at= lastModifiedAt,
         max_passanger = 0)
    }*/
}

@JsonClass(generateAdapter = true)
data class MasterDataModel(
    @SerializedName("id")
    var id: Int?,

    @SerializedName("customer_photo")
    var customer_photo: String?,

    @SerializedName("registration_number")
    var registration_number: String?,

    @SerializedName("customer_name")
    var customer_name: String?,

    @SerializedName("date_of_birth")
    var date_of_birth: String?,

    @SerializedName("address")
    var address: String?,

    @SerializedName("height")
    var height: String?,

    @SerializedName("sex")
    var sex: String?,

    @SerializedName("color_of_eyes")
    var color_of_eyes: String?,

    @SerializedName("date_issued")
    var date_issued: String?,

    @SerializedName("date_expire")
    var date_expire: String?,

    @SerializedName("type_of_competency-for")
    var type_of_competency_for: String?,

    @SerializedName("class_type")
    var class_type: String?,

    @SerializedName("types_of_competency")
    var types_of_competency: String?,

    @SerializedName("created_at")
    var created_at: String?,

    @SerializedName("last_modified_at")
    var last_modified_at: String?,

  /*  var country: String?,
    var phone_no: String?,
    var email: String?,
    var customer_number: String?,
    var nationality: String?,
    var expiration_date: String?,
    var comment: String?,
    var competency_name: String?,
    var competency_value_name: String?,
    var class_name: String?,*/
){
    /*internal  fun getMasterDataTable(): MasterTableData {
        return  MasterTableData(
            id= id,
        registration_no= registration_number,
        service_id= "",
        customer_name= customer_name,
        street= address,
        city= "",
        country= "",
        date_of_birth= date_of_birth,
        phone_no= "",
        email= "",
        height= height,
        issued_date= date_issued,
        customer_number= "",
        types_of_competency= types_of_competency,
        types_of_competency_for= type_of_competency_for,
        class_type= class_type,
        nationality= "",
        gender= sex,
        color_of_eyes= color_of_eyes,
        expiration_date= date_expire,
        photograph= customer_photo,
        comment= "",
        created_at= created_at,
        updated_at= last_modified_at,
        competency_name= "",
        competency_value_name= "",
        class_name= "",


        )
    }*/
}