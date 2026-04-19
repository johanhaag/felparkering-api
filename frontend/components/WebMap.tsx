import L from 'leaflet';
import { MapContainer, Marker, TileLayer } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import MapMover from './MapMover';
import Route from './Route';

const addresIconHtml = `
  <div style="width: 40px; height: 40px;">
    <svg viewBox="0 0 64 64" width="40" height="40" xmlns="http://www.w3.org/2000/svg">
      <g>
        <path d="M32 2C18 2 6 14 6 28c0 14 26 34 26 34s26-20 26-34C58 14 46 2 32 2z" fill="#E63946"/>
        <g transform="translate(20, 24)">
          <rect x="0" y="2" width="24" height="8" rx="2" ry="2" fill="#ffffff"/>
          <rect x="4" y="-4" width="16" height="8" rx="2" ry="2" fill="#ffffff"/>
          <circle cx="6" cy="10" r="3" fill="#ffffff"/>
          <circle cx="18" cy="10" r="3" fill="#ffffff"/>
        </g>
      </g>
    </svg>
  </div>
`;

const hqIconHtml = `
  <div style="width: 40px; height: 40px;">
    <svg viewBox="0 0 64 64" width="40" height="40" xmlns="http://www.w3.org/2000/svg">
      <g>
        <path d="M32 2C18 2 6 14 6 28c0 14 26 34 26 34s26-20 26-34C58 14 46 2 32 2z" fill="#2ECC71"/>
        <g transform="translate(20, 20)">
			<polygon points="12,-4 0,6 0,20 24,20 24,6" fill="#ffffff"/>
			<rect x="9" y="10" width="6" height="10" fill="#2ECC71"/>
      	</g>
      </g>
    </svg>
  </div>
`;

const addresIcon = L.divIcon({
    html: addresIconHtml,
    className: "",
    iconSize: [40, 40],
    iconAnchor: [20, 40],
});

const hqIcon = L.divIcon({
    html: hqIconHtml,
    className: "",
    iconSize: [40, 40],
    iconAnchor: [20, 40],
});

interface WebMapProps {
    adressPosition: [number, number];
    hqPosition: [number, number];
    onRouteReady: React.Dispatch<React.SetStateAction<{distance: number; duration: number}>>
}

export default function WebMap({ adressPosition, hqPosition, onRouteReady }: WebMapProps) {
    console.log(adressPosition);
    return (
        <MapContainer center={adressPosition} zoom={10} style={{ height: '50vh', width: '100%' }}>
            <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
            <Marker position={adressPosition} icon={addresIcon} />
            <Marker position={hqPosition} icon={hqIcon} />
            <Route
                start={hqPosition}
                end={adressPosition}
                weight={5}
                onReady={(fc) => {
                    const props = fc.features?.[0]?.properties;
                    onRouteReady?.(props?.summary);
                }}
            />
            <MapMover position={adressPosition} />
        </MapContainer>
    );
}