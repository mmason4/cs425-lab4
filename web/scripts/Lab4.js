var Lab4 = ( function() {

    return {

        convert: function(rates) {
            
            var userInput = $("#input").val();
            
            if(userInput.length == 0 || isNaN(userInput)) {
                alert("Invalid entry in the textbox!");
                return false;
            }
            else{
                var userNum = Number(userInput);
            }
            
            var presentDate = rates.date;
            var rates = rates.rates;
            for (var key in rates) {
                if (rates.hasOwnProperty(key)) {
                  var val = rates[key];
                  var roundedNum = (val * userNum).toFixed(2);
                  $("#output").append(key + " " + roundedNum + "</br>");
                }
              }
              $("#output").append("Based on " + presentDate + " exchange rates");

        },
        
        getConversion: function() {
            
            var that = this;
            
            $.ajax({
                url: 'latest',
                method: 'GET',
                dataType: 'json',
                success: function(response) {
                    that.convert(response);                    
                }
            });
            
        },
        
        init: function() {
            
            /* Output the current version of jQuery (for diagnostic purposes) */
            
            $('#output').html( "jQuery Version: " + $().jquery );
 
        }

    };

}());