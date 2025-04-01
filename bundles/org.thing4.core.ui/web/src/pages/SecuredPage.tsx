import {BasePage, BasePageProps} from "./BasePage.tsx";
import {AuthProvider, useAuthentication} from "../components/context/AuthProvider.tsx";

interface SecuredPageProps extends BasePageProps {
    authorizationRequired?: true;
}

function SecuredContent({title, children, authorizationRequired}: SecuredPageProps) {
    const {user, signIn} = useAuthentication();

    if (authorizationRequired && !user) {
        signIn();
        return <p>Redirecting to login...</p>;
    }

    return <BasePage title={title}>{children}</BasePage>;
}

export function SecuredPage(props : SecuredPageProps) {
    return (
        <AuthProvider>
            <SecuredContent {...props}>{props.children}</SecuredContent>
        </AuthProvider>
    );
};
