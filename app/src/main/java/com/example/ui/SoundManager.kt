package com.example.ui

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.sin
import kotlin.random.Random

object SoundManager {
    var volume: Float = 1.0f

    fun playSound(type: String) {
        Thread {
            try {
                when (type) {
                    "pistol" -> playPistolSound()
                    "ak47" -> playAK47Sound()
                    "shotgun" -> playShotgunSound()
                    "sniper" -> playSniperSound()
                    "hit" -> playHitSuccessSound()
                    "boss_teleport" -> playBossTeleportSound()
                    "boss_shoot" -> playBossShootSound()
                    "boss_hit" -> playBossHitSound()
                    "user_hit" -> playUserHitSound()
                    "game_over" -> playGameOverSound()
                    "victory" -> playVictorySound()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun playPistolSound() {
        val sampleRate = 22050
        val duration = 0.12f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val freq = 550f * (1f - t / duration) + 90f
            val angle = 2.0 * Math.PI * freq * t
            val wave = sin(angle)
            val noise = Random.nextFloat() * 2f - 1f
            val env = (1.0f - t / duration) * (1.0f - t / duration)
            val sample = (wave * 0.35f + noise * 0.65f) * env * 32767
            buffer[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playAK47Sound() {
        val sampleRate = 22050
        val duration = 0.18f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val freq = 380f * (1f - t / duration) + 60f
            val angle = 2.0 * Math.PI * freq * t
            val wave = if (sin(angle) > 0) 1f else -1f
            val noise = Random.nextFloat() * 2f - 1f
            val env = (1.0f - t / duration)
            val sample = (wave * 0.25f + noise * 0.75f) * env * 32767
            buffer[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playShotgunSound() {
        val sampleRate = 22050
        val duration = 0.32f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val thumpFreq = 160f * (1f - t / duration) + 30f
            val thumpAngle = 2.0 * Math.PI * thumpFreq * t
            val thump = sin(thumpAngle)
            val noise = Random.nextFloat() * 2f - 1f
            val env = (1.0f - t / duration) * (1.0f - t / duration)
            val sample = (thump * 0.18f + noise * 0.82f) * env * 32767
            buffer[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playSniperSound() {
        val sampleRate = 22050
        val duration = 0.55f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val thumpFreq = 200f * (1f - t / 0.15f).coerceAtLeast(0f) + 40f
            val thumpAngle = 2.0 * Math.PI * thumpFreq * t
            val thump = sin(thumpAngle)
            val noise = Random.nextFloat() * 2f - 1f
            
            val echoFreq = 800f
            val echoAngle = 2.0 * Math.PI * echoFreq * t
            val echo = sin(echoAngle) * 0.12f * (1f - t / duration)
            
            val env = (1.0f - t / duration)
            val sample = ((thump * 0.2f + noise * 0.6f) * (1f - t / 0.2f).coerceAtLeast(0f) + echo) * env * 32767
            buffer[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playHitSuccessSound() {
        val sampleRate = 22050
        val duration = 0.22f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val wave1 = sin(2.0 * Math.PI * 1046.5 * t)
            val wave2 = sin(2.0 * Math.PI * 1318.51 * t)
            val env = Math.exp(-7.0 * t)
            val sample = (wave1 * 0.5f + wave2 * 0.5f) * env * 32767
            buffer[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playBossTeleportSound() {
        val sampleRate = 22050
        val duration = 0.28f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val freq = 350f + 1100f * (t / duration)
            val angle = 2.0 * Math.PI * freq * t
            val env = sin(t / duration * Math.PI.toFloat())
            val sample = sin(angle) * env * 24000
            buffer[i] = sample.toInt().toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playBossShootSound() {
        val sampleRate = 22050
        val duration = 0.38f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val freq = 1100f - 750f * (t / duration)
            val angle = 2.0 * Math.PI * freq * t
            val wave = (freq * t % 1.0f) * 2f - 1f
            val env = (1.0f - t / duration)
            val sample = (sin(angle) * 0.45f + wave * 0.55f) * env * 21000
            buffer[i] = sample.toInt().toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playBossHitSound() {
        val sampleRate = 22050
        val duration = 0.32f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val freq = 140f - 60f * (t / duration)
            val angle = 2.0 * Math.PI * freq * t
            val noise = Random.nextFloat() * 2f - 1f
            val env = (1.0f - t / duration) * (1.0f - t / duration)
            val sample = (sin(angle) * 0.35f + noise * 0.65f) * env * 28000
            buffer[i] = sample.toInt().toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playUserHitSound() {
        val sampleRate = 22050
        val duration = 0.22f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val freq = 175f
            val wave1 = sin(2.0 * Math.PI * freq * t)
            val wave2 = sin(2.0 * Math.PI * (freq + 8.0f) * t)
            val env = (1.0f - t / duration)
            val sample = (wave1 * 0.5f + wave2 * 0.5f) * env * 24000
            buffer[i] = sample.toInt().toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playGameOverSound() {
        val sampleRate = 22050
        val duration = 0.75f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val stage = (t / duration * 4).toInt()
            val freq = when (stage) {
                0 -> 330f
                1 -> 294f
                2 -> 261f
                else -> 196f
            }
            val angle = 2.0 * Math.PI * freq * t
            val env = (1.0f - t / duration)
            val sample = sin(angle) * env * 24000
            buffer[i] = sample.toInt().toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playVictorySound() {
        val sampleRate = 22050
        val duration = 0.75f
        val numSamples = (sampleRate * duration).toInt()
        val buffer = ShortArray(numSamples)
        
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val stage = (t / duration * 4).toInt()
            val freq = when (stage) {
                0 -> 261f
                1 -> 330f
                2 -> 392f
                else -> 523f
            }
            val angle = 2.0 * Math.PI * freq * t
            val env = (1.0f - t / duration)
            val sample = sin(angle) * env * 24000
            buffer[i] = sample.toInt().toShort()
        }
        playBuffer(buffer, sampleRate)
    }

    private fun playBuffer(buffer: ShortArray, sampleRate: Int) {
        if (volume <= 0f) return
        val scaledBuffer = if (volume < 1f) {
            ShortArray(buffer.size) { i ->
                (buffer[i] * volume).toInt().coerceIn(-32768, 32767).toShort()
            }
        } else {
            buffer
        }
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(scaledBuffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(scaledBuffer, 0, scaledBuffer.size)
            track.play()
            
            Thread {
                try {
                    Thread.sleep((scaledBuffer.size * 1000L / sampleRate) + 150)
                    track.stop()
                    track.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
