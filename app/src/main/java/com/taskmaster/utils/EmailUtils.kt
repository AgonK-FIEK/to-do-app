package com.taskmaster.utils

import com.taskmaster.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailUtils {
    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "587"

    suspend fun sendPasswordResetCode(recipientEmail: String, code: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val props = Properties().apply {
                put("mail.smtp.host", SMTP_HOST)
                put("mail.smtp.port", SMTP_PORT)
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
            }

            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(BuildConfig.EMAIL_USERNAME, BuildConfig.EMAIL_PASSWORD)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(BuildConfig.EMAIL_USERNAME))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                subject = "TaskMaster Password Reset Code"
                setText("""
                    Hello,

                    You have requested a password reset for your TaskMaster account.

                    Your password reset code is: $code

                    This code will expire in 10 minutes.

                    Enter this code in the app to reset your password.

                    If you did not request this, please ignore this email.

                    Best regards,
                    TaskMaster Team
                """.trimIndent())
            }

            Transport.send(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(recipientEmail: String): Result<Unit> = sendPasswordResetCode(recipientEmail, generateResetCode())

    suspend fun send2FACode(recipientEmail: String, code: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val props = Properties().apply {
                put("mail.smtp.host", SMTP_HOST)
                put("mail.smtp.port", SMTP_PORT)
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
            }

            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(BuildConfig.EMAIL_USERNAME, BuildConfig.EMAIL_PASSWORD)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(BuildConfig.EMAIL_USERNAME))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                subject = "TaskMaster 2FA Verification Code"
                setText("""
                    Your TaskMaster verification code is: $code

                    This code will expire in 5 minutes.

                    If you did not request this, please ignore this email.
                """.trimIndent())
            }

            Transport.send(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateResetCode(): String {
        return (100000..999999).random().toString()
    }
}
