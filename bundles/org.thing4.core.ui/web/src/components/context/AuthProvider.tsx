import {createContext, useContext, useEffect, useState, ReactNode} from "react";
import {UserManager, WebStorageStateStore, User} from "oidc-client-ts";

const config = {
    authority: "https://your-identity-provider.com",
    client_id: "your-client-id",
    redirect_uri: "http://localhost:5173/callback",
    response_type: "code",
    scope: "openid profile email",
    post_logout_redirect_uri: "http://localhost:5173",
    userStore: new WebStorageStateStore({store: window.localStorage}),
};

const userManager = new UserManager(config);

interface AuthenticationContextType {
    user: User | null;
    signIn: () => void;
    signOut: () => void;
}

const AuthenticationContext = createContext<AuthenticationContextType | null>(null);

export const AuthProvider = ({children}: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        userManager.getUser().then((user) => {
            if (user && !user.expired) {
                setUser(user);
            }
        });

        userManager.events.addUserLoaded(setUser);
        userManager.events.addUserUnloaded(() => setUser(null));

        return () => {
            userManager.events.removeUserLoaded(setUser);
            userManager.events.removeUserUnloaded(() => setUser(null));
        };
    }, []);

    const signIn = () => userManager.signinRedirect();
    const signOut = () => userManager.signoutRedirect();

    return (
        <AuthenticationContext.Provider value={{user, signIn, signOut}}>
            {children}
        </AuthenticationContext.Provider>
    );
};

export const useAuthentication = () => {
    const context = useContext(AuthenticationContext);
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};