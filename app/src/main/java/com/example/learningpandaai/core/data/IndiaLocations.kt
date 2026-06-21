package com.example.learningpandaai.core.data

/**
 * Indian states & union territories with major cities/towns for onboarding and profile forms.
 * Cities are representative (capitals + large urban centers), sorted alphabetically per state.
 */
object IndiaLocations {

    private val citiesByState: Map<String, List<String>> = mapOf(
        "Andaman and Nicobar Islands" to listOf(
            "Port Blair", "Diglipur", "Mayabunder", "Rangat"
        ),
        "Andhra Pradesh" to listOf(
            "Amaravati", "Anantapur", "Guntur", "Kadapa", "Kakinada", "Kurnool",
            "Nellore", "Rajamahendravaram", "Tirupati", "Vijayawada", "Visakhapatnam"
        ),
        "Arunachal Pradesh" to listOf(
            "Itanagar", "Naharlagun", "Pasighat", "Tawang", "Ziro"
        ),
        "Assam" to listOf(
            "Dibrugarh", "Guwahati", "Jorhat", "Nagaon", "Silchar", "Tezpur", "Tinsukia"
        ),
        "Bihar" to listOf(
            "Arrah", "Begusarai", "Bhagalpur", "Darbhanga", "Gaya", "Muzaffarpur",
            "Patna", "Purnia", "Saharsa", "Samastipur"
        ),
        "Chandigarh" to listOf("Chandigarh"),
        "Chhattisgarh" to listOf(
            "Bhilai", "Bilaspur", "Durg", "Jagdalpur", "Korba", "Raipur", "Rajnandgaon"
        ),
        "Dadra and Nagar Haveli and Daman and Diu" to listOf(
            "Daman", "Diu", "Silvassa"
        ),
        "Delhi" to listOf(
            "Central Delhi", "Dwarka", "East Delhi", "Karol Bagh", "New Delhi",
            "North Delhi", "Rohini", "Saket", "South Delhi", "West Delhi"
        ),
        "Goa" to listOf(
            "Mapusa", "Margao", "Panaji", "Ponda", "Vasco da Gama"
        ),
        "Gujarat" to listOf(
            "Ahmedabad", "Anand", "Bhavnagar", "Gandhinagar", "Jamnagar", "Junagadh",
            "Rajkot", "Surat", "Vadodara", "Valsad"
        ),
        "Haryana" to listOf(
            "Ambala", "Faridabad", "Gurugram", "Hisar", "Karnal", "Kurukshetra",
            "Panipat", "Rohtak", "Sonipat", "Yamunanagar"
        ),
        "Himachal Pradesh" to listOf(
            "Bilaspur", "Dharamshala", "Hamirpur", "Kullu", "Mandi", "Manali",
            "Shimla", "Solan", "Una"
        ),
        "Jammu and Kashmir" to listOf(
            "Anantnag", "Baramulla", "Jammu", "Kathua", "Srinagar", "Udhampur"
        ),
        "Jharkhand" to listOf(
            "Bokaro", "Deoghar", "Dhanbad", "Giridih", "Hazaribagh", "Jamshedpur",
            "Ranchi"
        ),
        "Karnataka" to listOf(
            "Ballari", "Belagavi", "Bengaluru", "Bidar", "Davanagere", "Dharwad",
            "Gulbarga", "Hubballi", "Kalaburagi", "Mangaluru", "Mysuru", "Raichur",
            "Shivamogga", "Tumakuru", "Udupi"
        ),
        "Kerala" to listOf(
            "Alappuzha", "Kannur", "Kochi", "Kollam", "Kottayam", "Kozhikode",
            "Malappuram", "Palakkad", "Thiruvananthapuram", "Thrissur"
        ),
        "Ladakh" to listOf("Kargil", "Leh"),
        "Lakshadweep" to listOf("Kavaratti", "Minicoy"),
        "Madhya Pradesh" to listOf(
            "Bhopal", "Burhanpur", "Gwalior", "Indore", "Jabalpur", "Katni",
            "Ratlam", "Rewa", "Sagar", "Satna", "Ujjain"
        ),
        "Maharashtra" to listOf(
            "Ahmednagar", "Akola", "Amravati", "Aurangabad", "Chandrapur", "Jalgaon",
            "Kolhapur", "Latur", "Mumbai", "Nagpur", "Nanded", "Nashik", "Navi Mumbai",
            "Pune", "Sangli", "Solapur", "Thane", "Wardha"
        ),
        "Manipur" to listOf("Bishnupur", "Imphal", "Thoubal"),
        "Meghalaya" to listOf("Jowai", "Nongpoh", "Shillong", "Tura"),
        "Mizoram" to listOf("Aizawl", "Champhai", "Lunglei", "Saiha"),
        "Nagaland" to listOf("Dimapur", "Kohima", "Mokokchung", "Tuensang"),
        "Odisha" to listOf(
            "Balasore", "Berhampur", "Bhubaneswar", "Cuttack", "Puri", "Rourkela",
            "Sambalpur"
        ),
        "Puducherry" to listOf("Karaikal", "Mahe", "Puducherry", "Yanam"),
        "Punjab" to listOf(
            "Amritsar", "Bathinda", "Hoshiarpur", "Jalandhar", "Ludhiana", "Mohali",
            "Patiala", "Pathankot", "Sangrur"
        ),
        "Rajasthan" to listOf(
            "Ajmer", "Alwar", "Bharatpur", "Bhilwara", "Bikaner", "Jaipur", "Jodhpur",
            "Kota", "Sikar", "Udaipur"
        ),
        "Sikkim" to listOf("Gangtok", "Gyalshing", "Namchi"),
        "Tamil Nadu" to listOf(
            "Chennai", "Coimbatore", "Cuddalore", "Erode", "Madurai", "Salem",
            "Thanjavur", "Thoothukudi", "Tiruchirappalli", "Tirunelveli", "Tiruppur",
            "Vellore"
        ),
        "Telangana" to listOf(
            "Hyderabad", "Karimnagar", "Khammam", "Mahbubnagar", "Nizamabad",
            "Ramagundam", "Suryapet", "Warangal"
        ),
        "Tripura" to listOf("Agartala", "Dharmanagar", "Udaipur"),
        "Uttar Pradesh" to listOf(
            "Agra", "Aligarh", "Ayodhya", "Bareilly", "Ghaziabad", "Gorakhpur",
            "Jhansi", "Kanpur", "Lucknow", "Mathura", "Meerut", "Moradabad", "Muzaffarnagar",
            "Noida", "Prayagraj", "Saharanpur", "Varanasi"
        ),
        "Uttarakhand" to listOf(
            "Almora", "Dehradun", "Haridwar", "Haldwani", "Nainital", "Rishikesh",
            "Roorkee", "Rudrapur"
        ),
        "West Bengal" to listOf(
            "Asansol", "Bardhaman", "Darjeeling", "Durgapur", "Howrah", "Kharagpur",
            "Kolkata", "Malda", "Siliguri"
        )
    )

    /** All states and union territories, alphabetically sorted. */
    val states: List<String> = citiesByState.keys.sorted()

    fun citiesFor(state: String): List<String> = citiesByState[state].orEmpty()
}
