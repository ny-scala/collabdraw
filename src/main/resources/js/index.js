(function($){
  $(function() {
    var submit = $('#submit-drawing');
    submit.hide();
    $('#name').focus(function(){
      submit.fadeIn('slow');
    });
  });
})(jQuery);