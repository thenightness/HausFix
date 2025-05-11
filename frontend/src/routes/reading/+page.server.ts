export async function load() {
    const readings = await getReadings();
    return {
     readings,
    };
   }
function getReadings(){
    return [
		{
            id: '1234',
            uuid: '1234',
            firstName: '1234',
            lastName: '1234',
            birthDate: '1234',
            gender: 'D',
            dateOfReading: '1234',
            comment: '1234',
            meterId: '1234',
            substitute: true,
            meterCount: 123,
            kindOfMeter: 'HEIZUNG',
		},
		{
            id: '1234',
            uuid: '1234',
            firstName: '1234',
            lastName: '1234',
            birthDate: '1234',
            gender: 'D',
            dateOfReading: '1234',
            comment: '1234',
            meterId: '1234',
            substitute: true,
            meterCount: 123,
            kindOfMeter: 'HEIZUNG',
		},
	];
}