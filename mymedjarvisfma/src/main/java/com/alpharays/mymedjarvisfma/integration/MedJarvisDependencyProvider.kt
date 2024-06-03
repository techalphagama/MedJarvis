package com.alpharays.mymedjarvisfma.integration

object MedJarvisDependencyProvider {


    private lateinit var medJarvisFeatureApi: MedJarvisFeatureApi


    fun provideImpl(
        medJarvisFeatureApi: MedJarvisFeatureApi,
        ) {

        MedJarvisDependencyProvider.medJarvisFeatureApi = medJarvisFeatureApi

    }


    fun medJarvisFeature() = medJarvisFeatureApi


}