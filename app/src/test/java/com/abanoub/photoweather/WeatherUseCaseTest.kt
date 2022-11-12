package com.abanoub.photoweather

import com.abanoub.photoweather.business.entities.Response
import com.abanoub.photoweather.business.repositories.abstraction.WeatherRepository
import com.abanoub.photoweather.business.usecases.abstraction.WeatherUseCase
import com.abanoub.photoweather.business.usecases.impl.WeatherUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.exceptions.base.MockitoException
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherUseCaseTest {

    private lateinit var useCase: WeatherUseCase
    private lateinit var throwableUseCase: WeatherUseCase

    @Mock
    private lateinit var resultResponse: Response

    @Before
    fun setUp() {
        useCase = WeatherUseCaseImpl(repository)
        throwableUseCase = WeatherUseCaseImpl(throwableRepository)
    }

    @Test
    fun `getWeatherData() with success then return success response`() {
        runBlocking {

            //act
            val response = useCase.getWeatherData(5.5,5.5)

            var success: Response? = null
            var errorMessage: Throwable? = null

            response
                .catch { errorMessage = it }
                .collect { success = it }

            //Assert Authentication
            Assert.assertNotNull(success)
            Assert.assertNull(errorMessage)
            Assert.assertEquals(success, resultResponse)
        }
    }

    @Test
    fun `getWeatherData() with failure then return error message`() {
        runBlocking {

            //Act
            val response = throwableUseCase.getWeatherData(5.5,5.5)

            var success: Response? = null
            var errorMessage: Throwable? = null

            response
                .catch { errorMessage = it }
                .collect { success = it }

            //Assert Authentication
            Assert.assertNull(success)
            Assert.assertNotNull(errorMessage)
            Assert.assertEquals(errorMessage?.message, throwableResult.message)
        }
    }

    private var repository = object : WeatherRepository {
        override suspend fun getWeatherData(
            latitude: Double,
            longitude: Double
        ): Response = resultResponse
    }

    private val throwableResult = MockitoException("Unknown Error")
    private var throwableRepository = object : WeatherRepository {
        override suspend fun getWeatherData(
            latitude: Double,
            longitude: Double
        ): Response = throw throwableResult
    }
}
