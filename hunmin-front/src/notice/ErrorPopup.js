import React, { useState } from 'react';
import ErrorPopup from './ErrorPopup'; // 경로는 프로젝트 구조에 따라 조정

const SomeComponent = () => {
    const [errorMessage, setErrorMessage] = useState('');

    const handleError = () => {
        setErrorMessage('Something went wrong!');
    };

    const closeErrorPopup = () => {
        setErrorMessage('');
    };

    return (
        <div>
            <button onClick={handleError}>Trigger Error</button>
            <ErrorPopup message={errorMessage} onClose={closeErrorPopup} />
        </div>
    );
};

export default SomeComponent;
