import React, {useState, forwardRef, useImperativeHandle} from 'react';
import QRCode from 'qrcode.react';

const QrCodeComponent = forwardRef((props, ref) => {
    const [url, setUrl] = useState(props.url);
    useImperativeHandle(ref, () => ({
        setUrl: (newUrl) => {
            setUrl(newUrl);
        }
    }));
    return (
        <>
            <QRCode
                value={url}
                size={350}
                includeMargin={true}
                renderAs="svg"
                className={props.className}
            />
        </>
    );
})


export default QrCodeComponent;