import React from 'react';
import {RegisterFlow} from "../tools/enums";
import QrCodeComponent from "../tools/QrCodeComponent";

const SecretKeyQrCode = ({setCurrentPage}) => {
    function goBackToInstallation() {
        setCurrentPage(RegisterFlow.INSTALLATION);
    }

    return (
        <>

            <div className="container w-50 p-3 text-center bg-light border border-1 border-dark-subtle rounded-top-5 h2 mb-0 mt-5">
                <div className="row row-cols-5 rounded-3 ms-2">
                    <div className="col col-8 text-center d-flex justify-content-center align-items-center pe-0">
                        Scan this QR code, using Google Authenticator
                    </div>
                    <div className="col col-3 me-3 text-center"><img width="100px" height="100px"
                                                                     src="/images/google-authenticator-logo.svg"
                                                                     className="border bg-light border-1 border-dark-subtle rounded-5"
                                                                     alt="GA logo"/></div>
                </div>
            </div>
            <div className="container bg-light-subtle rounded-bottom-5 border border-1 border-dark-subtle border-top-0 w-50">
                <div className="row">
                    <QrCodeComponent
                        className="col py-4 rounded-4 bg-light"
                        url="quqP5VaHBSliXnkYzQe2Nr4+ej2ufLNuOF93PA+bngfN7yPX4Yv81Gu6tcYrZvfhz1aGQ3kqkxTTAIzWyjW/mj2WM/mk80fymSywFT81dVDLlfP+vsSn0LYZLANijaXeNF5J1iVD362Eq/mD8s/6WBVYCQXWPgqPrJWJYGhiiL35cX0q3x+ef17eA7C1qukypJLlbqAHHDEy6COhmw6aiskhWdX2nQ+HGbTUbSqoUy7jylpOzCS6sSSA1ZbeZEl0kpGatFTXXV8lQnWC3uxz4oBMA5v8wtp4NrpI5wDoCZCMYzko37ajg+H5Rnu6ikYtVjoFr1WwHLAK3K147Qw0qogmknYLDCHVZcLjDfN0kcKGe64Kw+MT7CNBdZMe9CYZIuHNHCTMg3MBdQq2kpKmn7p9G3vPhzWdhM8BxJ4i4Wmc9r4EDKnw8HHAV45w6bDHEMRVPWpLLCpoXhHwwYkR/nfQtTskXs6PM1TMdKKFJbgIixkkTlZYCSiglCyjR7w0gOS15EHNnTTz9suOyGXoibWSsk0sA6uUfe71yNqij5C4BBBNQ08pI8c49vNRd7GdtwpMZUI5qxOFYniQLLaysec4jEHHEc1jOQScYmKeJnWQsA0sLYeaaiEgRTQl4auah4YuZZEihT6qyh2bJ5VRhZ4bjEtLg4qES9xOJl5mwbmj+ElPNPieFZoq2qqx5IssDPaiP0O4HZCQwcUONfk88Bulb6MhKlYgpJej4O9bdvx4bArszyDf2QkrWXOInh5zAFzNsMqncjFTpBG8mAtr6uYBSZSGnXZqSNdEw8TzSUZc8D+Koa00TnE6CZkHHEnZ9LA66gWJD21/hXQiZoqMhGs3pGLlLFFURNtVYjc3XWnw9M/plljG6KP2HdYe/EmAXPAMVQUM9cqasYze7PAIQ4PXjdgciZg2gpVGUngOqRVmOcZOFqIl/jae1udD7PjoBgCFm06RI1gCQuQC55NhItEelHbEPHLVDaY2eWlzu8anxux9qt0B8Mqum1CqFlZXTbWwiSwa0ZPIeCMmqlh5/ddDSWzwD8dN7yPG97HDe/jhvfxV+A3fYlWOgxd9msAAAAASUVORK5CYII="/>
                </div>

            </div>
            <div className="container text-end mt-3 mr-3 w-50">
                <div className="row">
                    <button type="button" onClick={goBackToInstallation}
                            className="btn btn-dark fs-4 rounded-4 py-3 col-5 ">Go back
                    </button>
                    <button type="button" onClick={()=>{}}
                            className="btn btn-dark fs-4 rounded-4 py-3 col-5 offset-2">Got it!
                    </button>
                </div>
            </div>
        </>
    );
};

export default SecretKeyQrCode;