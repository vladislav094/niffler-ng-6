package guru.qa.niffler.service;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public enum ThreadSafeCookieStore implements CookieStore {
    INSTANCE;

    private final ThreadLocal<CookieStore> threadSafeCookieStore = ThreadLocal.withInitial(
            ThreadSafeCookieStore::inMemoryCookieStore
    );

    private static CookieStore inMemoryCookieStore() {
     return new CookieManager().getCookieStore();
    }

    @Override
    public void add(URI uri, HttpCookie httpCookie) {
        getStore().add(uri, httpCookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return getStore().get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return getStore().getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return getStore().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        return getStore().remove(uri, httpCookie);
    }

    @Override
    public boolean removeAll() {
        return getStore().removeAll();
    }

    private CookieStore getStore() {
        return threadSafeCookieStore.get();
    }

    public String cookieValue(String cookieName) {
        return getCookies().stream()
                .filter(c -> c.getName().equals(cookieName))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow();
    }
}
