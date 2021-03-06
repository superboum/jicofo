/*
 * Jicofo, the Jitsi Conference Focus.
 *
 * Copyright @ 2020 - present 8x8, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.jicofo.xmpp

import org.jitsi.config.JitsiConfig.Companion.legacyConfig
import org.jitsi.config.JitsiConfig.Companion.newConfig
import org.jitsi.metaconfig.config
import org.jitsi.metaconfig.optionalconfig
import org.jxmpp.jid.DomainBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.time.Duration

class XmppConfig {
    companion object {
        @JvmField
        val service = XmppServiceConnectionConfig()

        @JvmField
        val client = XmppClientConnectionConfig()

        @JvmField
        val config = XmppConfig()
    }
}

interface XmppConnectionConfig {
    val enabled: Boolean
    val hostname: String
    val port: Int
    val domain: DomainBareJid
    val username: Resourcepart
    val password: String?
    val replyTimeout: Duration
    val disableCertificateVerification: Boolean
}

class XmppServiceConnectionConfig : XmppConnectionConfig {
    override val enabled: Boolean by config {
        "jicofo.xmpp.service.enabled".from(newConfig)
    }

    override val hostname: String by config {
        "jicofo.xmpp.service.hostname".from(newConfig)
    }

    override val port: Int by config {
        "jicofo.xmpp.service.port".from(newConfig)
    }

    override val domain: DomainBareJid by config {
        "jicofo.xmpp.service.domain".from(newConfig).convertFrom<String> {
            JidCreate.domainBareFrom(it)
        }
    }

    override val username: Resourcepart by config {
        "jicofo.xmpp.service.username".from(newConfig).convertFrom<String> {
            Resourcepart.from(it)
        }
    }

    override val password: String? by optionalconfig {
        "jicofo.xmpp.service.password".from(newConfig)
    }

    override val replyTimeout: Duration by config {
        "jicofo.xmpp.service.reply-timeout".from(newConfig)
    }

    override val disableCertificateVerification: Boolean by config {
        "jicofo.xmpp.service.disable-certificate-verification".from(newConfig)
    }

    override fun toString(): String = "XmppServiceConnectionConfig[hostname=$hostname, port=$port, username=$username]"
}

class XmppClientConnectionConfig : XmppConnectionConfig {
    override val enabled: Boolean by config {
        "jicofo.xmpp.client.enabled".from(newConfig)
    }

    override val hostname: String by config {
        "jicofo.xmpp.client.hostname".from(newConfig)
    }

    override val port: Int by config {
        "jicofo.xmpp.client.port".from(newConfig)
    }

    /**
     * This is the domain used for login. Not necessarily the root XMPP domain.
     */
    override val domain: DomainBareJid by config {
        "jicofo.xmpp.client.domain".from(newConfig).convertFrom<String> {
            JidCreate.domainBareFrom(it)
        }
    }

    override val username: Resourcepart by config {
        "jicofo.xmpp.client.username".from(newConfig).convertFrom<String> {
            Resourcepart.from(it)
        }
    }

    override val password: String? by optionalconfig {
        "jicofo.xmpp.client.password".from(newConfig)
    }

    /**
     * This is the top-level domain hosted by the XMPP server (not necessarily the one used for login).
     */
    val xmppDomain: DomainBareJid by config {
        legacyXmppDomainPropertyName.from(newConfig).convertFrom<String> {
            JidCreate.domainBareFrom(it)
        }
        legacyXmppDomainPropertyName.from(legacyConfig).convertFrom<String> {
            JidCreate.domainBareFrom(it)
        }
    }

    val conferenceMucJid: DomainBareJid by config {
        "jicofo.xmpp.client.conference-muc-jid".from(newConfig).convertFrom<String> {
            JidCreate.domainBareFrom(it)
        }
        "default" { JidCreate.domainBareFrom("conference.$xmppDomain") }
    }

    override val replyTimeout: Duration by config {
        "jicofo.xmpp.client.reply-timeout".from(newConfig)
    }

    override val disableCertificateVerification: Boolean by config {
        "jicofo.xmpp.client.disable-certificate-verification".from(newConfig)
    }

    val clientProxy: DomainBareJid? by optionalconfig {
        "jicofo.xmpp.client.client-proxy".from(newConfig).convertFrom<String> {
            JidCreate.domainBareFrom(it)
        }
    }

    override fun toString(): String = "XmppClientConnectionConfig[hostname=$hostname, port=$port, username=$username]"

    companion object {
        const val legacyHostnamePropertyName = "org.jitsi.jicofo.HOSTNAME"
        const val legacyDomainPropertyName = "org.jitsi.jicofo.FOCUS_USER_DOMAIN"
        const val legacyUsernamePropertyName = "org.jitsi.jicofo.FOCUS_USER_NAME"
        const val legacyPasswordPropertyName = "org.jitsi.jicofo.FOCUS_USER_PASSWORD"
        const val legacyXmppDomainPropertyName = "org.jitsi.jicofo.XMPP_DOMAIN"
    }
}
