import React from 'react';
import QRCode from 'qrcode.react';

const QrCodeComponent = ({url, className}) => {
    return (
        <>
            <QRCode
                value={url}
                size={350}
                includeMargin={true}
                renderAs="svg"
                className={className}
            />
        </>
    );
};

export default QrCodeComponent;