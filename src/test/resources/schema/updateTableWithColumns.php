<?php return new class extends Migration {
    public function up()
    {
        Schema::table('users', function (\Illuminate\Database\Schema\Blueprint $table) {
              $table->id();
              $table->string('branch')->index();
              $table->integer('amount');
              $table->double('weight');
              $table->float('price');
              $table->timestamps();
              $table->string('<caret>');
          });
    }
}
