import React, { useEffect, useRef, useState } from 'react';
import { TextField, Paper, Box, Typography } from '@mui/material';

const KakaoMapSearch = ({ onLocationSelect }) => {
    const mapContainer = useRef(null);
    const [map, setMap] = useState(null);
    const [places, setPlaces] = useState([]);
    const markers = useRef([]);
    const [selectedPlace, setSelectedPlace] = useState(null); // 선택된 장소 상태

    useEffect(() => {
        const kakao = window.kakao;

        if (!kakao || !kakao.maps) {
            console.error('Kakao Maps API not loaded');
            return;
        }

        const mapInstance = new kakao.maps.Map(mapContainer.current, {
            center: new kakao.maps.LatLng(37.5665, 126.978), // 초기 위치
            level: 3,
        });

        setMap(mapInstance);

        return () => {
            setMap(null);
            setPlaces([]);
            removeMarkers();
        };
    }, []);

    const searchPlaces = (keyword) => {
        const kakao = window.kakao;
        const ps = new kakao.maps.services.Places();

        ps.keywordSearch(keyword, (data, status) => {
            if (status === kakao.maps.services.Status.OK) {
                setPlaces(data);
                moveMapToLocation(data[0]); // 첫 번째 장소로 지도 이동
                addMarkers(data, kakao);
            } else {
                setPlaces([]);
                removeMarkers();
            }
        });
    };

    const moveMapToLocation = (place) => {
        const kakao = window.kakao;
        const position = new kakao.maps.LatLng(place.y, place.x);
        map.setCenter(position); // 지도 위치 이동
    };

    const addMarkers = (places, kakao) => {
        removeMarkers();

        places.forEach(place => {
            const markerPosition = new kakao.maps.LatLng(place.y, place.x);
            const marker = new kakao.maps.Marker({
                position: markerPosition,
                title: place.place_name,
            });

            // 마커에 인포윈도우 추가
            const infowindow = new kakao.maps.InfoWindow({
                content: `<div style="padding:5px;">${place.place_name}</div>`, // 마커 위에 보여줄 내용
            });

            kakao.maps.event.addListener(marker, 'mouseover', () => {
                infowindow.open(map, marker); // 마커에 마우스 오버 시 인포윈도우 열기
            });

            kakao.maps.event.addListener(marker, 'mouseout', () => {
                infowindow.close(); // 마커에서 마우스 아웃 시 인포윈도우 닫기
            });

            kakao.maps.event.addListener(marker, 'click', () => {
                handlePlaceSelect(place); // 마커 클릭 시 장소 선택
            });

            marker.setMap(map);
            markers.current.push(marker);
        });
    };

    const removeMarkers = () => {
        markers.current.forEach(marker => marker.setMap(null));
        markers.current.length = 0;
    };

    const handlePlaceSelect = (place) => {
        const { y: latitude, x: longitude } = place;
        setSelectedPlace({
            name: place.place_name,
            latitude,
            longitude,
        });
        onLocationSelect({
            name: place.place_name,
            latitude,
            longitude,
        });
    };

    const handleSearch = (e) => {
        const keyword = e.target.value;
        if (keyword) {
            searchPlaces(keyword);
        } else {
            setPlaces([]);
            removeMarkers();
            setSelectedPlace(null); // 검색어가 없으면 선택된 장소 초기화
        }
    };

    return (
        <Paper elevation={3} style={{ padding: '20px', display: 'flex', flexDirection: 'column' }}>
            <TextField
                variant="outlined"
                placeholder="장소를 검색하세요..."
                onChange={handleSearch}
                style={{ marginBottom: '20px' }}
            />
            <Box ref={mapContainer} style={{ width: '100%', height: '300px' }} />
            {/* 선택된 장소 이름 표시 */}
            {selectedPlace && (
                <Typography variant="h6" style={{ marginTop: '10px' }}>
                    선택된 장소: {selectedPlace.name}
                </Typography>
            )}
        </Paper>
    );
};

export default KakaoMapSearch;
