package com.thongars.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

abstract class BaseTestClassNoPowerMock {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = TestCoroutineRule()

    val testCoroutineDispatcher = mainCoroutineRule.testCoroutineDispatcher

    @Before
    open fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    open fun tearDown() {
        unmockkAll()
    }

    protected fun readFile(fileName: String): String {

        val stringBuilder = StringBuilder()
        val inputStream = this.javaClass.classLoader?.getResourceAsStream(fileName)

        if (inputStream?.available() == 0) {
            throw FileNotFoundException("$fileName does not exist")
        }
        val reader = InputStreamReader(inputStream)
        try {
            var data = reader.read()
            while (data != -1) {
                val current = data.toChar()
                stringBuilder.append(current)
                data = reader.read()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }
}