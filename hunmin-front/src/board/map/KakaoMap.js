// KakaoMap.js
import { Map, MapMarker } from "react-kakao-maps-sdk";
import React from 'react';

const KakaoMap = ({ boards = [], mapLevel, mapCenter = { lat: 37.5665, lng: 126.978 } }) => {

    return (
        <div>
            {mapCenter.lat && mapCenter.lng ? (
                <Map
                    center={mapCenter}
                    style={{
                        width: '600px',
                        height: '500px',
                        borderRadius: '20px',
                    }}
                    level={mapLevel}
                >
                    {boards.length > 0 ? (
                        boards.map((board) => (
                            board.location && (
                                <MapMarker
                                    key={board.boardId}
                                    position={{ lat: board.latitude, lng: board.longitude }}
                                    onClick={() => window.location.href = `/board/${board.boardId}`}
                                >
                                    {board.title}
                                </MapMarker>
                            )
                        ))
                    ) : (
                        <div style={{ textAlign: 'center', marginTop: '20px' }}>게시글이 없습니다.</div>
                    )}
                </Map>
            ) : (
                <div style={{ textAlign: 'center', marginTop: '20px' }}>지도 데이터를 로드할 수 없습니다.</div>
            )}
        </div>
    );
};

export default KakaoMap;
