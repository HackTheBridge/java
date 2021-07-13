package io.ipinfo.api;

import io.ipinfo.api.cache.Cache;
import io.ipinfo.api.context.Context;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.ASNResponse;
import io.ipinfo.api.model.IPResponse;
import io.ipinfo.api.model.MapResponse;
import io.ipinfo.api.request.ASNRequest;
import io.ipinfo.api.request.IPRequest;
import io.ipinfo.api.request.MapRequest;
import okhttp3.OkHttpClient;

import java.util.List;

public class IPInfo {
    private final OkHttpClient client;
    private final Context context;
    private final String token;
    private final Cache cache;

    IPInfo(OkHttpClient client, Context context, String token, Cache cache) {
        this.client = client;
        this.context = context;
        this.token = token;
        this.cache = cache;
    }

    public static void main(String... args) {
        System.out.println("This library is not meant to be run as a standalone jar.");

        System.exit(0);
    }

    /**
     * Gets the builder for IPInfo
     *
     * @return IPInfoBuilder object
     */
    public static IPInfoBuilder builder() {
        return new IPInfoBuilder();
    }

    /**
     * Lookup IP information using the IP.
     *
     * @param ip the ip string to lookup - accepts both ipv4 and ipv6.
     * @return IPResponse response from the api.
     * @throws RateLimitedException an exception when your api key has been rate limited.
     */
    public IPResponse lookupIP(String ip) throws RateLimitedException {
        IPResponse response = cache.getIp(ip);
        if (response != null) {
            return response;
        }

        response = new IPRequest(client, token, ip).handle();
        response.setContext(context);

        cache.setIp(ip, response);
        return response;
    }

    /**
     * Lookup ASN information using the AS number.
     *
     * @param asn the asn string to lookup.
     * @return ASNResponse response from the api.
     * @throws RateLimitedException an exception when your api key has been rate limited.
     */
    public ASNResponse lookupASN(String asn) throws RateLimitedException {
        ASNResponse response = cache.getAsn(asn);
        if (response != null) {
            return response;
        }

        response = new ASNRequest(client, token, asn).handle();
        response.setContext(context);

        cache.setAsn(asn, response);
        return response;
    }

    /**
     * Get a map of a list of IPs.
     *
     * @param ips the list of IPs to map.
     * @return String the URL to the map.
     * @throws RateLimitedException an exception when your API key has been rate limited.
     */
    public String getMap(List<String> ips) throws RateLimitedException {
        MapResponse response = new MapRequest(client, token, ips).handle();
        return response.getReportUrl();
    }
}
