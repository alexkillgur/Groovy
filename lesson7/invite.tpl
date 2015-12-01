Street: $street, house: $house, flat: $flat
$city
$country
$postalCode
<% print "tel.: $phones.tel, mobile: $phones.mobile" %>

Dear $firstName $lastName!

We are glad to inform you that you passed next tests:
<% curses.each { key, value -> if ( value == true ) println "\t$key" } %>
You are welcome for interview in our office in $date
